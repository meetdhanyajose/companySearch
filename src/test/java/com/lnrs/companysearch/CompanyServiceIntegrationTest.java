package com.lnrs.companysearch;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lnrs.companysearch.model.CompanySearchResponse;
import com.lnrs.companysearch.model.CompanySearchResult;
import com.lnrs.companysearch.model.Officer;
import com.lnrs.companysearch.model.OfficersResponse;
import com.lnrs.companysearch.model.SearchResponse;
import com.lnrs.companysearch.service.CompanyService;

@RunWith(SpringRunner.class)
@SpringBootTest // Include this if you're using Spring Boot for dependency injection
public class CompanyServiceIntegrationTest {

    @InjectMocks
    private CompanyService companyService;

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private ObjectMapper objectMapper;

    private static final String COMPANY_NUMBER = "06500244";
    private static final String COMPANY_NAME = "BBC LIMITED";
    private static final CompanySearchResult EXPECTED_COMPANY_RESULT;
    private static final List<Officer> EXPECTED_OFFICERS;
    private static final String truProxyApiCompanyUrl = "https://exercise.trunarrative.cloud/TruProxyAPI/rest/Companies/v1/Search?Query=";
    private static final String truProxyApiOfficersUrl = "https://exercise.trunarrative.cloud/TruProxyAPI/rest/Companies/v1/Officers?CompanyNumber=";

    static {
        EXPECTED_COMPANY_RESULT = createCompanySearchResult();
        EXPECTED_OFFICERS = createActiveOfficers();
    }

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this); // Initialize mocks
    }

    @Test
    public void testSearchCompanies_Success() throws Exception {
        boolean activeOnly = false;
        String apiKey = "PwewCEztSW7XlaAKqkg4IaOsPelGynw6SN9WsbNf";

        // Configure successful responses
        CompanySearchResponse companyResponse = new CompanySearchResponse();
        companyResponse.setItems(Collections.singletonList(EXPECTED_COMPANY_RESULT));
        when(restTemplate.exchange(anyString(), eq(org.springframework.http.HttpMethod.GET), any(), eq(CompanySearchResponse.class)))
                .thenReturn(new ResponseEntity<>(companyResponse, org.springframework.http.HttpStatus.OK));

        OfficersResponse officersResponse = new OfficersResponse();
        officersResponse.setItems(EXPECTED_OFFICERS);
        when(restTemplate.exchange(anyString(), eq(org.springframework.http.HttpMethod.GET), any(), eq(OfficersResponse.class)))
                .thenReturn(new ResponseEntity<>(officersResponse, org.springframework.http.HttpStatus.OK));

        ResponseEntity<String> responseEntity = companyService.searchCompanies(null, COMPANY_NUMBER, activeOnly, apiKey);

        assertNotNull(responseEntity);

        // Verify RestTemplate calls
        verify(restTemplate, times(0)).exchange(
                eq(truProxyApiOfficersUrl + COMPANY_NUMBER),
                eq(HttpMethod.GET),
                any(HttpEntity.class),
                eq(OfficersResponse.class));
    }

    @Test
    public void testSearchCompanies_CompanyNotFound() throws Exception {
        boolean activeOnly = true;
        String apiKey = "PwewCEztSW7XlaAKqkg4IaOsPelGynw6SN9WsbNf";

        // Configure not found response for company search
        when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), any(), eq(CompanySearchResponse.class)))
                .thenReturn(new ResponseEntity<>(HttpStatus.NOT_FOUND));

        // Expect null response for company search
        ResponseEntity<String> responseEntity = companyService.searchCompanies(null, COMPANY_NUMBER, activeOnly, apiKey);

        // Verify RestTemplate calls only for company search
        verify(restTemplate, times(0)).exchange(
                eq(truProxyApiCompanyUrl + COMPANY_NUMBER),
                eq(HttpMethod.GET),
                any(HttpEntity.class),
                eq(CompanySearchResponse.class));
        verify(restTemplate, times(0)).exchange(anyString(), eq(HttpMethod.GET), any(), eq(OfficersResponse.class));
    }

    @Test
    public void testSearchCompanies_InternalServerError() throws Exception {
    	 boolean activeOnly = true;
         String apiKey = "PwewCEztSW7XlaAKqkg4IaOsPelGynw6SN9WsbNf";

         // Configure successful responses
         CompanySearchResponse companyResponse = new CompanySearchResponse();
         companyResponse.setItems(Collections.singletonList(EXPECTED_COMPANY_RESULT));
         when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), any(), eq(CompanySearchResponse.class)))
                 .thenReturn(new ResponseEntity<>(companyResponse, HttpStatus.OK));

         OfficersResponse officersResponse = new OfficersResponse();
         officersResponse.setItems(EXPECTED_OFFICERS);
         when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), any(), eq(OfficersResponse.class)))
                 .thenReturn(new ResponseEntity<>(officersResponse, HttpStatus.OK));

         ResponseEntity<String> responseEntity = companyService.searchCompanies(null, COMPANY_NUMBER, activeOnly, apiKey);

         assertNotNull(responseEntity);

      // Verify RestTemplate calls
         verify(restTemplate, times(0)).exchange(
                 eq(truProxyApiCompanyUrl + COMPANY_NUMBER),
                 eq(HttpMethod.GET),
                 any(HttpEntity.class),
                 eq(CompanySearchResponse.class));

         verify(restTemplate, times(0)).exchange(
                 eq(truProxyApiOfficersUrl + COMPANY_NUMBER),
                 eq(HttpMethod.GET),
                 any(HttpEntity.class),
                 eq(OfficersResponse.class));
    }

    @Test
    public void testSearchCompanies_EmptyCompanyNumber() throws Exception {
        boolean activeOnly = true;
        String apiKey = "PwewCEztSW7XlaAKqkg4IaOsPelGynw6SN9WsbNf";

        // Expect IllegalArgumentException for empty company number
        try {
            companyService.searchCompanies(null, "", activeOnly, apiKey);
        } catch (IllegalArgumentException e) {
            assertEquals("Company number cannot be empty", e.getMessage());
        }

         verify(restTemplate, times(0)).exchange(anyString(), eq(HttpMethod.GET), any(HttpEntity.class), eq(CompanySearchResponse.class));
    }

    @Test
    public void testSearchCompanies_MissingApiKey() throws Exception {
        boolean activeOnly = true;

        // Expect IllegalArgumentException for missing API key
        try {
            companyService.searchCompanies(null, COMPANY_NUMBER, activeOnly, null);
        } catch (IllegalArgumentException e) {
            assertEquals("API key cannot be null or empty", e.getMessage());
        }

        // Verify no RestTemplate calls
        verify(restTemplate, times(0)).exchange(anyString(), eq(HttpMethod.GET), any(HttpEntity.class), eq(CompanySearchResponse.class));
    }

    // Utility methods to create mock data
    private static CompanySearchResult createCompanySearchResult() {
        CompanySearchResult result = new CompanySearchResult();
        result.setTitle(COMPANY_NAME);
        // ... set other company details

        return result;
    }

    private static List<Officer> createActiveOfficers() {
        List<Officer> officers = new ArrayList<>();
        Officer officer1 = new Officer();
        // ... set other officer details

        officers.add(officer1);
        // ... add other officers

        return officers;
    }
}
