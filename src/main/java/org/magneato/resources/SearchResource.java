package org.magneato.resources;

import java.io.IOException;
import java.util.ResourceBundle;

import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.magneato.managed.ManagedElasticClient;
import org.magneato.utils.Pagination;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/*
 * http://localhost:9200/my-index/_search?q=halloween&sort=metadata.create_date:asc
 * http://localhost:9200/my-index/_search?q=metadata.display_template:home
 */
@Path("/")
public class SearchResource {
	private final ManagedElasticClient repository;
	private ResourceBundle resourceBundle;
	private final Logger log = LoggerFactory.getLogger(this.getClass()
			.getName());

	public SearchResource(ManagedElasticClient repository) {
		this.repository = repository;
		this.resourceBundle = ResourceBundle.getBundle("common.searchbundle");
	}

	@GET
	@Path("/search/{query}/{page}/{size}")
	@Produces(MediaType.TEXT_HTML)
	public Object get(@PathParam("query") String query,
			@PathParam("page") int page, @PathParam("size") int size )
			throws IOException {
		if (page < 0) {
			page = 0;
		}
		if (size < 0 || size > 100) {
			size = 10;
		}	
		Pagination pagination = repository.generalSearch(page * size, size,
				query);
		return new SearchView(pagination, resourceBundle);
	}

	/*
	http://localhost:9090/facets/metadata.edit_template=tripreport/activity_c,technical_c.orientation/0/10
	 */
	@GET
	@Path("/facets/{query}/{facets}/{page}/{size}")
	@Produces(MediaType.TEXT_HTML)
	public Object getFacets(@PathParam("query") String query, @PathParam("facets") String facets,
			@PathParam("page") int page, @PathParam("size") int size )
			throws IOException {
		if (page < 0) {
			page = 0;
		}
		if (size < 0 || size > 100) {
			size = 10;
		}	
		if (facets.trim().isEmpty()) {
			facets = null;
		}
		Pagination pagination = repository.search(page * size, size, query, facets);
		return new SearchView(pagination, resourceBundle);
	}

	
	@POST
	@Path("/search")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Produces(MediaType.TEXT_HTML)
	public Object search(@FormParam("query") String query,
			@FormParam("from") int from, @FormParam("size") int size)
			throws IOException {

		Pagination pagination = repository.generalSearch(from, size, query);
		return new SearchView(pagination, resourceBundle);
	}

}
