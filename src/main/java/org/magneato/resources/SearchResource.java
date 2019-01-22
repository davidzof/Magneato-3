package org.magneato.resources;

import java.io.IOException;

import javax.ws.rs.*;
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
	private ManagedElasticClient repository;
	private final Logger log = LoggerFactory.getLogger(this.getClass()
			.getName());

	public SearchResource(ManagedElasticClient repository) {
		this.repository = repository;
	}

	@POST
	@Path("/search")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Produces(MediaType.TEXT_HTML)
	public Object search(
			@FormParam("query") String query,
			@FormParam("from") int from,
			@FormParam("size") int size)
			throws IOException {

		Pagination pagination = repository.generalSearch(from, size, query);
		return new SearchView(pagination);
	}


}
