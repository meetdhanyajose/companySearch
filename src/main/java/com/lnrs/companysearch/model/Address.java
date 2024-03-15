package com.lnrs.companysearch.model;

public class Address {
	private String locality;
	private String postal_code;
    private String premises;
    private String address_line_1;
    private String country;   

    public Address() {
    }

    public Address(String premises, String postalCode, String country, String locality, String addressLine1) {
        this.premises = premises;
        this.postal_code = postalCode;
        this.country = country;
        this.locality = locality;
        this.address_line_1 = addressLine1;
    }

    public String getPremises() {
        return premises;
    }

    public void setPremises(String premises) {
        this.premises = premises;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getLocality() {
        return locality;
    }

    public void setLocality(String locality) {
        this.locality = locality;
    }
	public String getPostal_code() {
		return postal_code;
	}

	public void setPostal_code(String postal_code) {
		this.postal_code = postal_code;
	}

	public String getAddress_line_1() {
		return address_line_1;
	}

	public void setAddress_line_1(String address_line_1) {
		this.address_line_1 = address_line_1;
	}

}
