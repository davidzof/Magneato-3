package org.magneato.resources;

import org.magneato.utils.Pagination;
import org.magneato.utils.StringHelper;

import com.fasterxml.jackson.databind.JsonNode;

public class SearchView extends ContentView {
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