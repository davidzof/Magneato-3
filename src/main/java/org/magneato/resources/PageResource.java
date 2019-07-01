package org.magneato.resources;

import static org.magneato.utils.StringHelper.toSlug;
import io.dropwizard.views.View;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.security.RolesAllowed;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
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

import org.magneato.MagneatoConfiguration;
import org.magneato.managed.ManagedElasticClient;
import org.magneato.service.MetaData;
import org.magneato.service.Template;
import org.magneato.utils.PageUtils;
import org.magneato.utils.PermissionsChecker;
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
	// TODO: does this need to be per thread? - what did we do elsewhere?
	private final ObjectMapper objectMapper = new ObjectMapper();

	private final Logger log = LoggerFactory.getLogger(this.getClass()
			.getName());
	private final PageUtils pageUtils;

	public PageResource(MagneatoConfiguration configuration,
			ManagedElasticClient repository) {
		this.templates = configuration.getTemplates();
		this.repository = repository;
		this.pageUtils = new PageUtils();

		/*
		 * Insert default page if it doesn't exist already
		 */
		String body = repository.get("1");
		if (body == null) {
			log.info("creating default page");
			// perms = CRUx|CRUx|CRxx|xxxx
			body = "{\"title\":\"Home Page\",\"content\":\"Welcome to Magneato CMS\",\"metadata\":{\"edit_template\":\"simple\",\"display_template\":\"simple\",\"create_date\":\"2018-01-01 00:01:01\",\"ip_addr\":\"127.0.0.1\",\"owner\":\"admin\",\"canonical_url\":\"index\",\"groups\":[\"editors\"],\"perms\":61120}}";
			repository.insert("1", body);
		}
	}

	/** default page, insert this at startup **/
	@GET
	@Produces(MediaType.TEXT_HTML)
	public Object index() throws IOException {
		return get("1", "index", null);
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
	@Path("/{id}/{uri}")
	@Produces(MediaType.TEXT_HTML)
	public Object get(@PathParam("id") String id, @PathParam("uri") String uri,
			@Context SecurityContext security) throws IOException {
		log.debug("get " + id + "/" + uri);

		if (uri == null || uri.isEmpty()) {
			log.debug("default page");
			// default page?
		}

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

		if (!canonicalURL.equals(uri)) {
			// redirect to canonical url
			URI redirectUrl = UriBuilder.fromUri(id + "/" + canonicalURL)
					.build();
			return Response.seeOther(redirectUrl).build();
		}

		String viewTemplate = jsonNode.get("metadata").get("display_template")
				.asText();

		return new PageView(body, "display." + viewTemplate, repository, id,
				uri);
	}

	@DELETE
	@Path("/{id}/{uri}")
	@Produces(MediaType.APPLICATION_JSON)
	public Object delete(@PathParam("id") String id,
			@PathParam("uri") String uri, @Context SecurityContext security) {
		String errMsg = null;

		String body = repository.get(id);
		if (body != null) {
			try {
				JsonNode metadata;

				/*
				 * Check, if ADMIN group - like superuser
				 */
				metadata = objectMapper.readTree(body).get("metadata");
				String owner = metadata.get("owner").asText();
				List<String> groups = new ArrayList<String>();
				JsonNode groupNode = metadata.get("groups");
				for (int i = 0; i < groupNode.size(); i++) {
					groups.add(groupNode.get(i).asText());
				}// for
				int perms = metadata.get("perms").asInt();

				if (PermissionsChecker
						.canDelete(security, owner, groups, perms)) {
					// delete any images first
					JsonNode files = objectMapper.readTree(body).get("files");

					// TODO don't allow file to be deleted with images attached
					if (files != null && files.size() > 0) {
						for (int i = 0; i < files.size(); i++) {
							errMsg = "Cannot Delete: Please edit and delete all attachments first";

						}// for
					} else {
						if (repository.delete(id) != null) {
							String message = "Page deleted " + uri;
							// redirect to Home Page
							return "{\"url\":\"/" + "\", \"message\":\""
									+ message + "\"}";
						}
					}
				} else {

					errMsg = "You don't have permission to delete " + uri;
				}
			} catch (IOException e) {
				log.equals(e.getMessage());
				errMsg = e.getMessage();
			}
		} else {
			errMsg = "No such page" + id + "/" + uri;
		}

		return "{\"error\":\"" + errMsg + "\"}";
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
	@GET
	@RolesAllowed({ "ADMIN", "EDITOR" })
	@Path("/create")
	@Produces(MediaType.TEXT_HTML)
	public View create(
			@DefaultValue("false") @QueryParam("clone") boolean clone,
			@DefaultValue("false") @QueryParam("child") boolean child,
			@QueryParam("editTemplate") String editTemplate,
			@QueryParam("displayTemplate") String displayTemplate,
			@Context HttpServletRequest request,
			@Context SecurityContext security) {
		log.debug("create(" + editTemplate + ", " + displayTemplate + ")");

		if (clone || child) {
			String id = PageUtils.getId(request.getHeader("referer"));
			if (id != null) {
				String body = repository.get(id); // get parent
				if (body != null) {
					// clone is always a child page
					int perms = 0;
					try {
						// only clone edit/display template, and fields
						// marked as clonable
						JsonNode metadata = objectMapper.readTree(body).get(
								"metadata");
						editTemplate = metadata.get("edit_template").asText();
						displayTemplate = metadata.get("display_template")
								.asText();
						perms = metadata.get("perms").asInt();

					} catch (IOException e) {
						log.error(e.getMessage());
						return new ErrorView("404-error");
					}

					log.debug("parent " + id);
					// child page inherits
					// edit_template/display template plus sets parent
					// relation
					MetaData metaData = new MetaData()
							.setEditTemplate(editTemplate)
							.setViewTemplate(displayTemplate)
							.setPerms(perms)
							.setIPAddr(request.getRemoteAddr()).addRelation(id)
							.setOwner(security.getUserPrincipal().getName());

					EditView view;
					if (clone) {
						// we need to set meta data here.
						// TODO use body that we read above !!!!
						body = pageUtils.cloneContent(body);
						log.debug("cloned page, adding " + body);
						view = new EditView(body, metaData);
					} else {
						view = new EditView(metaData);
					}

					return view;
				}
			}
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

			return new EditView(metaData);
		}

		return new CreatePageView(templates);
	}

	@GET
	@RolesAllowed("EDITOR")
	@Produces(MediaType.TEXT_HTML)
	@Path("/edit/{id}/{uri}")
	public View edit(@PathParam("id") String id, @PathParam("uri") String uri) {
		log.debug("edit " + id + "/" + uri);

		String body = repository.get(id);
		if (body == null) {
			// this can't work because we don't know display template - call
			// create page

			return create(false, false, null, null, null, null);
		}

		String editTemplate = null;
		try {
			JsonNode jsonNode = objectMapper.readTree(body);
			editTemplate = jsonNode.get("metadata").get("edit_template")
					.asText();
		} catch (IOException e) {
			log.error(e.getMessage());
			return new ErrorView("404-error", id + "/" + uri);

		}

		log.debug("edit returning " + body);
		return new EditView(id + "/" + uri, body, editTemplate);
	}

	// https://www.jqueryscript.net/other/Beautiful-JSON-Viewer-Editor.html
	@GET
	@RolesAllowed("ADMIN")
	@Produces(MediaType.TEXT_HTML)
	@Path("/editRaw/{id}/{uri}")
	public View editRaw(@PathParam("id") String id, @PathParam("uri") String uri) {
		String url = id + "/" + uri;
		log.debug("editRaw " + url);

		String body = repository.get(id);
		if (body != null) {
			log.debug("editRaw " + body);
			return new FTLView("json", body, url);
		}

		return new FTLView("error", "Can't find page " + uri);
	}

	/**
	 * Update asset {uri:([a-zA-Z\\-_\\.0-9]*)}
	 * 
	 * @param uri
	 * @param body
	 * @param security
	 * @return
	 */
	@PUT
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	@RolesAllowed("EDITOR")
	@Path("/save/{id}/{uri}")
	public String update(@PathParam("id") String id,
			@PathParam("uri") String uri, String body,
			@Context SecurityContext security) {
		log.debug("Updating " + uri + " " + body);
		// permissions: ADMIN, MODERATOR, EDITOR, if page exists already, page
		// owner - needs meta data
		// meta data is: create date, owner, editTemplate, displayTemplate
		// Security.canCreate(uri);

		String data = null;
		try {
			JsonNode jsonNode = objectMapper.readTree(body);
			if (security.isUserInRole("ADMIN")) {
				// TODO fix this
				System.out
						.println("************ >>> admin can update meta data");
			}

			body = jsonNode.toString();
			repository.insert(id, body);
			data = "{\"url\":\"/" + id + "/" + uri + "\"}";
		} catch (IOException e) {
			log.error(e.getMessage());
			data = "{\"error\":\"" + e.getMessage() + "\"}";
		}
		return data;
	}

	/**
	 * Create a new "page" asset. If admin, Metadata can be edited Must be an
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

				((com.fasterxml.jackson.databind.node.ObjectNode) jsonNode)
						.set("metadata",
								objectMapper.readTree(metaData.toJson()));
			} else {
				((com.fasterxml.jackson.databind.node.ObjectNode) jsonNode
						.get("metadata")).put("canonical_url", pageTitle);
			}
			body = jsonNode.toString();

			String id = repository.insert(body);
			pageTitle = id + "/" + pageTitle;
			data = "{\"url\":\"/" + pageTitle + "\"}";
		} catch (IOException e) {
			log.error(e.getMessage());
			data = "{\"error\":\"" + e.getMessage() + "\"}";
		}

		return data;
	}
}