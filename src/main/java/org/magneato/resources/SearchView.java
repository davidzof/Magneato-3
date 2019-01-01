package org.magneato.resources;

import io.dropwizard.views.View;

import java.io.IOException;

import org.magneato.utils.Pagination;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class SearchView extends View {
	private final Logger log = LoggerFactory.getLogger(this.getClass()
			.getName());
	private final ObjectMapper objectMapper = new ObjectMapper();
	Pagination paginater;

	public SearchView(Pagination paginater) {
		super("search.ftl");
		
		this.paginater = paginater;
	}
	
	public Pagination getPaginator() {
		return paginater;
	}
	
	// TODO move to helper
	public JsonNode toJsonNode(String json) {
		JsonNode jsonNode = null;
		try {
			jsonNode = objectMapper.readTree(json);
		} catch (IOException e) {
			log.error("Something went wrong reading json " + e.getMessage()
					+ " " + json);
		}

		return jsonNode;
	}
}