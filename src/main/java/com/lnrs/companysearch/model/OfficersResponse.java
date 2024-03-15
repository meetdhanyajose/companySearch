package com.lnrs.companysearch.model;

import java.util.List;

public class OfficersResponse {
    private String etag;
    private Links links;
    private String kind;
    private int items_per_page;
    private List<Officer> items;
    private int inactive_count;
    private int total_results;
    private int resigned_count;

    public OfficersResponse() {
    }

    public OfficersResponse(String etag, Links links, String kind, int itemsPerPage, List<Officer> items, int inactiveCount, int totalResults, int resignedCount) {
        this.etag = etag;
        this.links = links;
        this.kind = kind;
        this.items_per_page = itemsPerPage;
        this.items = items;
        this.inactive_count = inactiveCount;
        this.total_results = totalResults;
        this.resigned_count = resignedCount;
    }

    public String getEtag() {
        return etag;
    }

    public void setEtag(String etag) {
        this.etag = etag;
    }

    public Links getLinks() {
        return links;
    }

    public void setLinks(Links links) {
        this.links = links;
    }

    public String getKind() {
        return kind;
    }

    public void setKind(String kind) {
        this.kind = kind;
    }

    public List<Officer> getItems() {
        return items;
    }

    public void setItems(List<Officer> items) {
        this.items = items;
    }

	public int getItems_per_page() {
		return items_per_page;
	}

	public void setItems_per_page(int items_per_page) {
		this.items_per_page = items_per_page;
	}

	public int getInactive_count() {
		return inactive_count;
	}

	public void setInactive_count(int inactive_count) {
		this.inactive_count = inactive_count;
	}

	public int getTotal_results() {
		return total_results;
	}

	public void setTotal_results(int total_results) {
		this.total_results = total_results;
	}

	public int getResigned_count() {
		return resigned_count;
	}

	public void setResigned_count(int resigned_count) {
		this.resigned_count = resigned_count;
	}
}
