package org.magneato.resources;

import io.dropwizard.views.View;

import org.magneato.utils.Pagination;
import org.magneato.utils.StringHelper;

import com.fasterxml.jackson.databind.JsonNode;

public class SearchView extends View {
	Pagination paginater;

	public SearchView(Pagination paginater) {
		super("search.ftl");
		
		this.paginater = paginater;
	}
	
	public Pagination getPaginator() {
		return paginater;
	}
	
	public JsonNode toJsonNode(String json) {
		return StringHelper.toJsonNode(json);
	}
}