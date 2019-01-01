package org.magneato.utils;


import java.util.List;

/**
 * Paging tool class
 */
public class Pagination {
	long total;
	List<String> results;

	public long getTotal() {
		return total;
	}

	public void setTotal(long total) {
		this.total = total;
	}

	public List<String> getResults() {
		return results;
	}

	public void setDataList(List<String> results) {
		this.results = results;
	}

}