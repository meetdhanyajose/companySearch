package com.lnrs.companysearch.model;

import java.util.List;

public class CompanySearchResponse {
    private int page_number;
    private String kind;
    private int total_results;
    private List<CompanySearchResult> items;

    public CompanySearchResponse() {
    }

    public CompanySearchResponse(int page_number, String kind, int total_results, List<CompanySearchResult> items) {
        this.page_number = page_number;
        this.kind = kind;
        this.total_results = total_results;
        this.items = items;
    }

    public int getPage_number() {
		return page_number;
	}

	public void setPage_number(int page_number) {
		this.page_number = page_number;
	}

	public int getTotal_results() {
		return total_results;
	}

	public void setTotal_results(int total_results) {
		this.total_results = total_results;
	}

	public String getKind() {
        return kind;
    }

    public void setKind(String kind) {
        this.kind = kind;
    }

    public List<CompanySearchResult> getItems() {
        return items;
    }

    public void setItems(List<CompanySearchResult> items) {
        this.items = items;
    }
}
