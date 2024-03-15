package com.lnrs.companysearch.model;

import java.util.List;

public class SearchResponse {
    private int total_results;
    private List<CompanySearchResult> items;

    public int getTotal_results() {
        return total_results;
    }

    public void setTotal_results(int total_results_of_companies) {
        this.total_results = total_results_of_companies;
    }

	public List<CompanySearchResult> getItems() {
		return items;
	}

	public void setItems(List<CompanySearchResult> items) {
		this.items = items;
	}
}
