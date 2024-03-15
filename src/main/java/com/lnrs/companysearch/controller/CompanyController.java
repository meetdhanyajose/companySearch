package com.lnrs.companysearch.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.lnrs.companysearch.model.SearchRequest;
import com.lnrs.companysearch.service.CompanyService;

@RestController
@RequestMapping("/api")
public class CompanyController {
    private final CompanyService companyService;

    public CompanyController(CompanyService companyService) {
        this.companyService = companyService;
    }

    @PostMapping("/company/search")
    public ResponseEntity<Object> searchCompanies(
            @RequestBody SearchRequest request,
            @RequestHeader("x-api-key") String apiKey) {

        // Call service to search for companies
        ResponseEntity<String> responseEntity = companyService.searchCompanies(request.getCompanyName(), request.getCompanyNumber(), request.isActiveOnly(), apiKey);

        // Check if response entity is not null
        if (responseEntity != null) {
            // Return the ResponseEntity from the service method
            return ResponseEntity.ok(responseEntity.getBody());
        } else {
            // Return 500 Internal Server Error if response entity is null
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to fetch companies");
        }
    }
}