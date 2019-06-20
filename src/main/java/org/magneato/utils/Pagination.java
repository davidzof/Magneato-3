package org.magneato.utils;

import java.util.HashMap;
import java.util.List;

import org.apache.commons.lang3.tuple.Pair;

/**
 * Paging tool class
 */
public class Pagination {
	private long total;
	private long current;
	private long size;
	private String query;
	private String facets;
	private String editTemplate;
	private List<String> results;
	private HashMap<String, List<Pair<String, Long>>> facetResults;

	public String getQuery() {
		return query;
	}

	public void setQuery(String query) {
		this.query = query;
	}
	
	public String getEditTemplate() {
		return editTemplate;
	}

	public void setEditTemplate(String editTemplate) {
		this.editTemplate = editTemplate;
	}

	public String getFacets() {
		return facets;
	}

	public void setFacets(String facets) {
		this.facets = facets;
	}

	public long getSize() {
		return size;
	}

	public void setSize(long size) {
		this.size = size;
	}

	public long getTotal() {
		return total;
	}

	public void setTotal(long total) {
		this.total = total;
	}

	public long getCurrent() {
		return current;
	}

	public void setCurrent(long current) {
		this.current = current;
	}

	public List<String> getResults() {
		return results;
	}

	public void setResults(List<String> results) {
		this.results = results;
	}

	public void setFacetResults(
			HashMap<String, List<Pair<String, Long>>> facetResults) {		
		this.facetResults = facetResults;
	}

	public HashMap<String, List<Pair<String, Long>>> getFacetResults() {
		return this.facetResults;
	}
}