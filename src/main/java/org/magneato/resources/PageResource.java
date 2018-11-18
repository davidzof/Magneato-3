package org.magneato.resources;

import io.dropwizard.views.View;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.text.Normalizer;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Pattern;

import javax.annotation.security.RolesAllowed;
import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.core.UriBuilder;

import org.apache.commons.io.FilenameUtils;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;
import org.magneato.MagneatoConfiguration;
import org.magneato.managed.ManagedElasticClient;
import org.magneato.service.MetaData;
import org.magneato.service.Template;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

// https://github.com/wdawson/dropwizard-auth-example/blob/master/pom.xml
/*
 * Basic CRUD stuff READ (, WRITE, DELETE, UPDATE (PUT)
 */

@Path("/")
public class PageResource {
	private final List<Template> templates;
	private ManagedElasticClient repository;
	private final ObjectMapper objectMapper = new ObjectMapper();

	private final Logger log = LoggerFactory.getLogger(this.getClass()
			.getName());

	private final static String IMAGEPATH = "/library/images";
	private String imageDir = null;

	public PageResource(MagneatoConfiguration configuration,
			ManagedElasticClient repository) {
		this.templates = configuration.getTemplates();
		this.repository = repository;

		Map<String, String> uriMappings = configuration
				.getAssetsConfiguration().getResourcePathToUriMappings();
		for (Map.Entry<String, String> entry : uriMappings.entrySet()) {
			System.out.println("Key = " + entry.getKey() + ", Value = "
					+ entry.getValue());
		}// TODO remove this

		Map<String, String> overrides = configuration.getAssetsConfiguration()
				.getOverrides();
		for (Map.Entry<String, String> entry : overrides.entrySet()) {
			if (IMAGEPATH.equals(entry.getKey())) {
				imageDir = entry.getValue() + "/";
			}
		}// for
	}

	/**
	 * Display a normal web page. The response should be cached.
	 * 
	 * @param uri
	 *            - name of resource to be displayed
	 * @param security
	 *            - security context
	 * @return Redirect object or Freemarker view
	 * @throws IOException 
	 */
	@GET
	@Path("{uri}")
	@Produces(MediaType.TEXT_HTML)
	public Object get(@PathParam("uri") String uri,
			@Context SecurityContext security) throws IOException {
		log.debug("get " + uri);

		if (uri == null || uri.isEmpty()) {
			log.debug("default page");
			// default page?
		}

		String id = uri.substring(uri.lastIndexOf('.') + 1);
		String body = repository.get(id);
		if (body == null) {
			// no contents, create page instead
			return new ErrorView("404-error", uri);
		}

		log.debug("get " + body);

		// check url is canonical and issue a 301 if not
		JsonNode jsonNode = objectMapper.readTree(body);
		String canonicalURL = jsonNode.get("metadata").get("canonical_url")
				.asText();

		String stem = uri.substring(0, uri.lastIndexOf('.'));
		if (!canonicalURL.equals(stem)) {
			// redirect to canonical url
			URI redirectUrl = UriBuilder.fromUri(canonicalURL + "." + id)
					.build();
			return Response.seeOther(redirectUrl).build();
		}

		String viewTemplate = jsonNode.get("metadata").get("display_template")
				.asText();
		return new PageView(body, "display." + viewTemplate, repository, uri);
	}

	/**
	 * Create a new page, displays a form where you can select the edit and
	 * display template to use
	 * <p>
	 * - you need to be an editor to do this - editTemplate can be specified, in
	 * this case default (first) display template is used - displayTemplate can
	 * be specified
	 * <p>
	 * If the editTemplate and/or displayTemplate are specified we pass directly
	 * to the edit page
	 */
	@RolesAllowed({ "ADMIN", "EDITOR" })
	@GET
	@Path("create{uri : (/uri)?}")
	@Produces(MediaType.TEXT_HTML)
	public View create(@PathParam("uri") String uri,
			@QueryParam("editTemplate") String editTemplate,
			@QueryParam("displayTemplate") String displayTemplate,
			@QueryParam("clone") boolean clone,
			@QueryParam("child") boolean child,
			@Context HttpServletRequest request,
			@Context SecurityContext security) {
		log.debug("create(" + uri + ", " + editTemplate + ", "
				+ displayTemplate + ", " + clone + ")");
		
		String current = request.getRequestURL().toString();
		String referrer = request.getHeader("referer");
		URL url;
		try {
			url = new URL(current);
			String protocol = url.getProtocol();
			String authority = url.getAuthority();
			current = String.format("%s://%s", protocol, authority);
			if (referrer.startsWith(current)) {
				log.debug("*** Own Domain");
				// ok here we can check for an id
				// page could be:
				// index <- do we allow this case?
				// index.1
				// index.BYzeHmcBsZHloJKdriPu
			}
		} catch (MalformedURLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		

		}
		
		log.debug("cloning " + referrer + " , " + current); // e.g. http://localhost:9090/cima-delle-rossette-versant-ouest.BYzeHmcBsZHloJKdriPu
		String id = referrer.substring(referrer.lastIndexOf('.') + 1);
		
		if (clone) {
			// clone is always a child page
			
			log.debug("cloning " + referrer);
			id = referrer.substring(referrer.lastIndexOf('.') + 1);
			log.debug("parent " + id);
			String body = repository.get(id);
			if (body != null) {
				try {
					// try and clone, don't clone any attachments, or children, only clone edit/display template, not title
					JsonNode jsonNode = objectMapper.readTree(body);
					editTemplate = jsonNode.get("metadata")
							.get("edit_template").asText();
				} catch (IOException e) {
					log.error(e.getMessage());
					return new ErrorView("404-error", uri);

				}

				log.debug("cloned page, returning " + body);
				return new EditView("", body, editTemplate);
			}

		} else if (child) {
			 id = referrer.substring(referrer.lastIndexOf('.') + 1);
			log.debug("parent " + id); // we should probably check it exists in the repo ?
		
			
		}

		if (editTemplate != null && !editTemplate.isEmpty()) {
			if (displayTemplate == null) {
				displayTemplate = editTemplate;
			}
			// we know enough about the page to go straight into editing
			MetaData metaData = new MetaData().setEditTemplate(editTemplate)
					.setViewTemplate(displayTemplate)
					.setIPAddr(request.getRemoteAddr())
					.setOwner(security.getUserPrincipal().getName());

			return new EditView(uri, metaData);
		}

		return new CreatePageView(templates);
	}

	@GET
	@RolesAllowed("EDITOR")
	@Produces(MediaType.TEXT_HTML)
	@Path("edit/{uri}")
	public View edit(@PathParam("uri") String uri) {
		log.debug("edit " + uri);

		String id = uri.substring(uri.lastIndexOf('.') + 1);
		String body = repository.get(id);
		if (body == null) {
			// this can't work because we don't know display template - call
			// create page

			return create(uri, null, null, false, false, null, null);
		}

		String editTemplate = null;
		try {
			JsonNode jsonNode = objectMapper.readTree(body);
			editTemplate = jsonNode.get("metadata").get("edit_template")
					.asText();
		} catch (IOException e) {
			log.error(e.getMessage());
			return new ErrorView("404-error", uri);

		}

		log.debug("edit returning " + body);
		return new EditView(uri, body, editTemplate);
	}

	/**
	 * Update asset
	 * 
	 * @param uri
	 * @param body
	 * @param security
	 * @return
	 */
	@PUT
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	@Path("/save/{uri:([a-zA-Z\\-_\\.0-9]*)}")
	public String update(@PathParam("uri") String uri, String body,
			@Context SecurityContext security) {
		log.debug("Updating " + uri + " " + body);
		// permissions: ADMIN, MODERATOR, EDITOR, if page exists already, page
		// owner - needs meta data
		// meta data is: create date, owner, editTemplate, displayTemplate
		// Security.canCreate(uri);

		if (security.isUserInRole("ADMIN")) {
			System.out.println("************ >>> admin can update meta data");
		}

		repository.insert(uri, body);
		String data = null;
		data = "{\"url\":\"/" + uri + "\"}";
		return data;

	}

	/**
	 * Create a new "page" asset. If admin Metadata can be edited Must be an
	 * Editor to create new pages
	 * 
	 * @param body
	 *            - json form data to be saved
	 * @return
	 */
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	@Path("/save")
	public String save(String body, @Context HttpServletRequest request,
			@Context SecurityContext security) {
		log.debug("Saving " + body);

		
		if (!security.isUserInRole("EDITOR")) {
			log.warn("Not logged in or not editor");
			return "{\"error\":\"You are not logged in or don't have editor permissions, see your sysadmin\"}";
		}

		String data = null;

		try {
			JsonNode jsonNode = objectMapper.readTree(body);
			String pageTitle = jsonNode.get("title").asText();
			pageTitle = toSlug(pageTitle);
			if (!security.isUserInRole("ADMIN")) {
				// create fresh metadata
				MetaData metaData = new MetaData();
				String editTemplate = jsonNode.get("metadata")
						.get("edit_template").asText();
				metaData.setEditTemplate(editTemplate).setViewTemplate("")
						.setIPAddr(request.getRemoteAddr())
						.setOwner(security.getUserPrincipal().getName())
						.setCanonicalURL(pageTitle);

				((ObjectNode) jsonNode).set("metadata",
						objectMapper.readTree(metaData.toJson()));
			} else {

				((ObjectNode) jsonNode.get("metadata")).put("canonical_url",
						pageTitle);
			}
			body = jsonNode.toString();

			String id = repository.insert(body);
			pageTitle = pageTitle + "." + id;
			data = "{\"url\":\"/" + pageTitle + "\"}";
		} catch (IOException e) {
			log.error(e.getMessage());
			data = "{\"error\":\"" + e.getMessage() + "\"}";
		}

		return data;
	}

	// https://gitlab.com/zloster/dropwizard-static
	// https://github.com/dropwizard-bundles/dropwizard-configurable-assets-bundle
	// TODO - what if image not jpg, thumb will be wrong format !
	@POST
	@Path("upload")
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	@Produces(MediaType.APPLICATION_JSON)
	public String upload(
			@FormDataParam("files") final InputStream fileInputStream,
			@FormDataParam("files") final FormDataContentDisposition contentDispositionHeader) {
		
		String fileName = contentDispositionHeader.getFileName();
		log.debug("filename " + fileName + " imageDir " + imageDir);

		if (imageDir == null) {
			log.warn("image directory not configured in config.yml");
			return null;
		}
		
		// store images in a subdir based on up to the first x letters of the filename, avoids putting too many files in one directory
		String subDir = FilenameUtils.getBaseName(fileName);
		// TODO make configurable for big sites
		if (subDir.length() > 3) {
				subDir = fileName.substring(0,3) + "/";
		}
		
		java.nio.file.Path outputPath = FileSystems.getDefault().getPath(
				imageDir + subDir, fileName);
		
		// create a thumbnail
		long len = 0;
		try {
			// make the directory, if it doesn't exist
			Files.createDirectories(outputPath.getParent());
			
			len = Files.copy(fileInputStream, outputPath);
			String name = imageDir + subDir + fileName;
			BufferedImage img = new BufferedImage(100, 100,
					BufferedImage.TYPE_INT_RGB);
			img.createGraphics().drawImage(
					// TODO, thumbs always square???
					ImageIO.read(new File(name)).getScaledInstance(100, 100,
							Image.SCALE_SMOOTH), 0, 0, null);

			String thumbName = imageDir + subDir + "thumb_" + fileName;
			ImageIO.write(img, "jpg", new File(thumbName)); // thumbs always jgps... this won't work for other types
		} catch (IOException e) {
			log.warn("upload " + e.getMessage());
		}

		String url = IMAGEPATH + "/" + subDir + fileName;
		String thumbUrl = IMAGEPATH + "/" + subDir + "thumb_" + fileName;

		return "{\"files\":[{\"url\":\""
				+ url
				+ "\",\"thumbnailUrl\":\""
				+ thumbUrl
				+ "\",\"name\":\""
				+ fileName
				+ "\",\"size\":\""+len+"\",\"type\":\"image/png\",\"deleteUrl\":\"delete/"
				+ fileName + "\",\"deleteType\":\"DELETE\"}]}";
	}

	// https://github.com/slugify/slugify
	private static final Pattern NONLATIN = Pattern.compile("[^\\w-]");
	private static final Pattern WHITESPACE = Pattern.compile("[\\s]");
	private static final Pattern EDGESDHASHES = Pattern.compile("(^-|-$)");

	public static String toSlug(String input) {
		String nowhitespace = WHITESPACE.matcher(input).replaceAll("-");
		String normalized = Normalizer.normalize(nowhitespace,
				Normalizer.Form.NFD);
		String slug = NONLATIN.matcher(normalized).replaceAll("");
		slug = EDGESDHASHES.matcher(slug).replaceAll("");
		return slug.toLowerCase(Locale.ENGLISH);
	}
}