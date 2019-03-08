package org.magneato.utils;

import java.util.List;

/**
 * Paging tool class
 */
public class Pagination {
	long total;
	long current;
	long size;
	String query;
	String facets;
	List<String> results;

	public String getQuery() {
		return query;
	}

	public void setQuery(String query) {
		this.query = query;
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
}