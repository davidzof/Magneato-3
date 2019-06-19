package org.magneato.resources;

import org.magneato.utils.Pagination;
import org.magneato.utils.StringHelper;

import com.fasterxml.jackson.databind.JsonNode;

import java.util.ResourceBundle;

public class SearchView extends ContentView {
	Pagination paginater;
	ResourceBundle resourceBundle;

	public SearchView(Pagination paginater, ResourceBundle resourceBundle) {
		super("search.ftl");
		
		this.paginater = paginater;
		this.resourceBundle = resourceBundle;
	}
	
	public Pagination getPaginator() {
		return paginater;
	}
	
	public JsonNode toJsonNode(String json) {
		return StringHelper.toJsonNode(json);
	}

	public String message(String key) {
		System.out.println("key " + key);
		System.out.println("rb " + resourceBundle);
		return resourceBundle.getString(key);
	}
}