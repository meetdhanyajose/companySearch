package com.lnrs.companysearch.model;

import java.util.List;

public class CompanySearchResult {
	private String company_number;
	private String company_type;
	private String title;
    private String company_status;
    private String date_of_creation;    
    private Address address;
    private List<Officer> officers;

    public CompanySearchResult() {
    }

    public CompanySearchResult(String companyStatus, String dateOfCreation, String companyNumber, String title, String companyType, Address address, List<Officer> officers) {
        this.company_status = companyStatus;
        this.date_of_creation = dateOfCreation;
        this.company_number = companyNumber;
        this.title = title;
        this.company_type = companyType;
        this.address = address;
        this.officers=officers;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCompany_status() {
		return company_status;
	}

	public void setCompany_status(String company_status) {
		this.company_status = company_status;
	}

	public String getDate_of_creation() {
		return date_of_creation;
	}

	public void setDate_of_creation(String date_of_creation) {
		this.date_of_creation = date_of_creation;
	}

	public String getCompany_number() {
		return company_number;
	}

	public void setCompany_number(String company_number) {
		this.company_number = company_number;
	}

	public String getCompany_type() {
		return company_type;
	}

	public void setCompany_type(String company_type) {
		this.company_type = company_type;
	}

	public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

	public List<Officer> getOfficers() {
		return officers;
	}

	public void setOfficers(List<Officer> officers) {
		this.officers = officers;
	}
}
