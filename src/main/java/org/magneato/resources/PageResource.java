package org.magneato.resources;

import io.dropwizard.views.View;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.security.Principal;
import java.text.Normalizer;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Pattern;

import javax.annotation.security.RolesAllowed;
import javax.imageio.ImageIO;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.SecurityContext;

import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;
import org.magneato.MagneatoConfiguration;
import org.magneato.managed.ManagedElasticClient;
import org.magneato.service.Template;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

// https://github.com/wdawson/dropwizard-auth-example/blob/master/pom.xml
/*
 * Basic CRUD stuff READ (, WRITE, DELETE, UPDATE (PUT)
 */

@Path("/")
public class PageResource {
	private final List<Template> templates;
	private ManagedElasticClient repository;

	private final Logger log = LoggerFactory.getLogger(this.getClass()
			.getName());

	private final static String IMAGEPATH = "/library/images";
	private String imageDir = null;

	public PageResource(MagneatoConfiguration configuration, ManagedElasticClient repository) {
		this.templates = configuration.getTemplates();
		this.repository = repository;

		log.debug("configuration");

		Map<String, String> uriMappings = configuration
				.getAssetsConfiguration().getResourcePathToUriMappings();
		for (Map.Entry<String, String> entry : uriMappings.entrySet()) {
			System.out.println("Key = " + entry.getKey() + ", Value = "
					+ entry.getValue());
		}

		Map<String, String> overrides = configuration.getAssetsConfiguration()
				.getOverrides();
		for (Map.Entry<String, String> entry : overrides.entrySet()) {
			if (IMAGEPATH.equals(entry.getKey())) {
				imageDir = entry.getValue() + "/";
			}
		}// for
	}

	// How do we handle home (default) web page? - I think we'll simply force
	// the ID to 0 or something
	@GET
	@Path("")
	@Produces(MediaType.TEXT_HTML)
	public View getDefault(@Context SecurityContext security) {
		return get("null", security);
	}

	/**
	 * Display a normal web page
	 * 
	 * @param uri
	 *            - name of resource to be displayed
	 * @param security
	 *            - security context
	 * @return Freemarker view
	 */
	@GET
	@Path("{uri}")
	@Produces(MediaType.TEXT_HTML)
	public View get(@PathParam("uri") String uri,
			@Context SecurityContext security) {

		log.debug("get " + uri);

		if (uri == null || uri.isEmpty()) {
			log.debug("default page");
			// default page?
		}

		// TODO: use Elastic
		String body = repository.get(uri);
		if (body == null) {
			// no contents, create page instead
			return new ErrorView("404-error", uri);
		}

		return new PageView(body, "display.simple");
	}

	/**
	 * Create a new page, displays a form where you can select the edit and
	 * display template to use
	 * 
	 * - you need to be an editor to do this - editTemplate can be specified, in
	 * this case default (first) display template is used - displayTemplate can
	 * be specified
	 * 
	 * If the editTemplate and/or displayTemplate are specified we pass directly
	 * to the edit page
	 */
	@RolesAllowed("ADMIN")
	@GET
	@Path("create{uri : (/uri)?}")
	@Produces(MediaType.TEXT_HTML)
	public View create(@PathParam("uri") String uri,
			@QueryParam("editTemplate") String editTemplate,
			@QueryParam("displayTemplate") String displayTemplate) {
		log.debug("create(" + uri + ", " + editTemplate + ", "
				+ displayTemplate + ")");

		if (editTemplate != null && !editTemplate.isEmpty()) {
			if (displayTemplate == null) {
				displayTemplate = editTemplate;
			}
			return new EditView(uri);
		}
		return new CreatePageView(templates);
	}

	@RolesAllowed("ADMIN")
	@GET
	@Produces(MediaType.TEXT_HTML)
	@Path("edit/{uri}")
	public View edit(@PathParam("uri") String uri) {
		log.debug("edit " + uri);
		String body = repository.get(uri);
		if (body == null) {
			// this can't work because we don't know display template - call
			// create page
			return create(uri, null, null);
		}
		log.debug(">>> edit returning " + body);
		return new EditView(uri, body);
	}

	/**
	 * @param uri
	 *            - lower case alphanumeric
	 * @param body
	 *            - json form data to be saved
	 * @return
	 */
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	@Path("/save{p:/?}{uri:([a-zA-Z\\-\\.0-9]*)}")
	public String saveAsset(@PathParam("uri") String uri, String body,
			@Context SecurityContext security) {
		log.debug(">>> Saving " + uri + " " + body);
		String data = null;

		ObjectMapper objectMapper = new ObjectMapper();

		// TODO: tests remove this
		Principal principal = security.getUserPrincipal();
		if (principal != null) {
			System.out.println(security.getUserPrincipal().getName());
		}

		try {
			JsonNode jsonNode = objectMapper.readTree(body);
			String pageTitle = jsonNode.get("title").asText();
			pageTitle = toSlug(pageTitle);

			if (uri.isEmpty()) {
				String id = repository.insert(body);
				pageTitle = pageTitle + "." + id;
				data = "{\"url\":\"/" + pageTitle + "\"}";
			} else {
				repository.insert(uri, body);
				data = "{\"url\":\"/" + uri + "\"}";
			}

			log.debug("returning " + data);

		} catch (IOException e) {
			log.error(e.getMessage());
			data = "{\"error\":\"" + e.getMessage() + "\"}";
		}

		return data;
	}

	// https://gitlab.com/zloster/dropwizard-static
	// https://github.com/dropwizard-bundles/dropwizard-configurable-assets-bundle
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

		java.nio.file.Path outputPath = FileSystems.getDefault().getPath(
				imageDir, fileName);
		System.out.println("output path " + outputPath.getFileName());

		// create a thumbnail
		try {
			Files.copy(fileInputStream, outputPath);
			String name = imageDir + fileName;
			BufferedImage img = new BufferedImage(100, 100,
					BufferedImage.TYPE_INT_RGB);
			img.createGraphics().drawImage(
					ImageIO.read(new File(name)).getScaledInstance(100, 100,
							Image.SCALE_SMOOTH), 0, 0, null);

			String thumbName = imageDir + "thumb_" + fileName;
			ImageIO.write(img, "jpg", new File(thumbName));
		} catch (IOException e) {
			log.warn("upload " + e.getMessage());
		}

		String url = IMAGEPATH + "/" + fileName;
		String thumbUrl = IMAGEPATH + "/thumb_" + fileName;

		return "{\"files\":[{\"url\":\""
				+ url
				+ "\",\"thumbnailUrl\":\""
				+ thumbUrl
				+ "\",\"name\":\""
				+ fileName
				+ "\",\"size\":\"12345\",\"type\":\"image/png\",\"deleteUrl\":\"delete/"
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