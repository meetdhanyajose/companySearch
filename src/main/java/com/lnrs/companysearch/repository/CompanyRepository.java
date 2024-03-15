package com.lnrs.companysearch.repository;

import com.lnrs.companysearch.model.Address;
import com.lnrs.companysearch.model.CompanySearchResult;
import com.lnrs.companysearch.model.Officer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Repository
public class CompanyRepository {

    // JDBC connection parameters
    @Value("${spring.datasource.url}")
    private String URL;
    @Value("${spring.datasource.username}")
    private String USERNAME;
    @Value("${spring.datasource.password}")
    private String PASSWORD;

    public CompanySearchResult searchCompanies(String companyNumber) {
        CompanySearchResult companySearchResult = new CompanySearchResult();
        List<Officer> officers = new ArrayList<>();

        try (Connection connection = DriverManager.getConnection(URL, USERNAME, PASSWORD)) {

            // Query to retrieve company details along with its address
            String companySql = "SELECT c.company_number, c.title, c.date_of_creation, c.company_status, c.company_type, " +
                    "a.premises AS address_premises, a.address_line_1, a.locality, a.postal_code, a.country " +
                    "FROM companies c " +
                    "JOIN addresses a ON c.address_id = a.address_id " +
                    "WHERE c.company_number = ?";
            try (PreparedStatement companyStatement = connection.prepareStatement(companySql)) {
                // Set parameter for the PreparedStatement
                companyStatement.setString(1, companyNumber);

                // Execute the query and get the result set
                try (ResultSet companyResultSet = companyStatement.executeQuery()) {
                    // Process the result set for company details
                    if (companyResultSet.next()) {
                        companySearchResult.setCompany_number(companyResultSet.getString("company_number"));
                        companySearchResult.setTitle(companyResultSet.getString("title"));
                        companySearchResult.setCompany_status(companyResultSet.getString("company_status"));
                        companySearchResult.setCompany_type(companyResultSet.getString("company_type"));
                        companySearchResult.setDate_of_creation(companyResultSet.getString("date_of_creation"));

                        // Construct the address object
                        Address address = new Address();
                        address.setPremises(companyResultSet.getString("address_premises"));
                        address.setAddress_line_1(companyResultSet.getString("address_line_1"));
                        address.setLocality(companyResultSet.getString("locality"));
                        address.setPostal_code(companyResultSet.getString("postal_code"));
                        address.setCountry(companyResultSet.getString("country"));
                        companySearchResult.setAddress(address);
                    }
                }
            }

            // Query to retrieve officers along with their addresses
            String officerSql = "SELECT o.name AS officer_name, o.officer_role, o.appointed_on, o.resigned_on, " +
                    "a.locality AS address_locality, a.postal_code, a.premises, a.address_line_1, a.country " +
                    "FROM officers o " +
                    "JOIN addresses a ON o.address_id = a.address_id " +
                    "WHERE o.company_number = ?";
            try (PreparedStatement officerStatement = connection.prepareStatement(officerSql)) {
                // Set parameter for the PreparedStatement
                officerStatement.setString(1, companyNumber);

                // Execute the query and get the result set
                try (ResultSet officerResultSet = officerStatement.executeQuery()) {
                    // Process the result set for officers
                    while (officerResultSet.next()) {
                        Officer officer = new Officer();
                        officer.setName(officerResultSet.getString("officer_name"));
                        officer.setOfficer_role(officerResultSet.getString("officer_role"));
                        officer.setAppointed_on(officerResultSet.getString("appointed_on"));
                        officer.setResigned_on(officerResultSet.getString("resigned_on"));

                        // Construct the address object for the officer
                        Address officerAddress = new Address();
                        officerAddress.setLocality(officerResultSet.getString("address_locality"));
                        officerAddress.setPostal_code(officerResultSet.getString("postal_code"));
                        officerAddress.setPremises(officerResultSet.getString("premises"));
                        officerAddress.setAddress_line_1(officerResultSet.getString("address_line_1"));
                        officerAddress.setCountry(officerResultSet.getString("country"));

                        officer.setAddress(officerAddress);

                        // Add the officer to the list
                        officers.add(officer);
                    }
                }
            }

            // Set the officers list in the company search result
            companySearchResult.setOfficers(officers);

        } catch (SQLException e) {
            e.printStackTrace();
            // Handle exceptions
        }

        return companySearchResult;
    }
}
