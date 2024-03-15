package com.lnrs.companysearch.model;

public class SearchRequest {

    private String companyName;
    private String companyNumber;
    private boolean activeOnly;

    // Constructors, getters, and setters

    public SearchRequest() {
        // Default constructor
    }

    public SearchRequest(String companyName, String companyNumber, boolean activeOnly) {
        this.companyName = companyName;
        this.companyNumber = companyNumber;
        this.activeOnly = activeOnly;
    }

    // Getters and setters for fields

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getCompanyNumber() {
        return companyNumber;
    }

    public void setCompanyNumber(String companyNumber) {
        this.companyNumber = companyNumber;
    }

    public boolean isActiveOnly() {
        return activeOnly;
    }

    public void setActiveOnly(boolean activeOnly) {
        this.activeOnly = activeOnly;
    }
}
