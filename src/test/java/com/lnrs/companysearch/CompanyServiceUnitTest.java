package com.lnrs.companysearch;

import static org.junit.Assert.assertEquals;
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
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lnrs.companysearch.model.CompanySearchResponse;
import com.lnrs.companysearch.model.CompanySearchResult;
import com.lnrs.companysearch.model.Officer;
import com.lnrs.companysearch.model.OfficersResponse;
import com.lnrs.companysearch.model.SearchResponse;
import com.lnrs.companysearch.service.CompanyService;

public class CompanyServiceUnitTest {

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private CompanyService companyService;

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
    	 // Initialize mocks manually
        restTemplate = Mockito.mock(RestTemplate.class);
        objectMapper = Mockito.mock(ObjectMapper.class);

        // Initialize the class under test with the mocks
        companyService = new CompanyService(restTemplate, objectMapper, null);

        // Configure mock response from restTemplate
        CompanySearchResponse companyResponse = new CompanySearchResponse();
        companyResponse.setItems(Collections.singletonList(EXPECTED_COMPANY_RESULT));
        when(restTemplate.exchange(anyString(), eq(org.springframework.http.HttpMethod.GET), any(), eq(CompanySearchResponse.class)))
                .thenReturn(new ResponseEntity<>(companyResponse, org.springframework.http.HttpStatus.OK));

        OfficersResponse officersResponse = new OfficersResponse();
        officersResponse.setItems(EXPECTED_OFFICERS);
        when(restTemplate.exchange(anyString(), eq(org.springframework.http.HttpMethod.GET), any(), eq(OfficersResponse.class)))
                .thenReturn(new ResponseEntity<>(officersResponse, org.springframework.http.HttpStatus.OK));
    }

    @Test
    public void testSearchCompanies() throws Exception {
        boolean activeOnly = true;
        String apiKey = "PwewCEztSW7XlaAKqkg4IaOsPelGynw6SN9WsbNf";

        ResponseEntity<String> responseEntity = companyService.searchCompanies(COMPANY_NAME, COMPANY_NUMBER, activeOnly, apiKey);

        // Assert that the response contains expected company and officer data
        String expectedJson = objectMapper.writeValueAsString(createExpectedResponse(EXPECTED_COMPANY_RESULT, EXPECTED_OFFICERS));
        assertEquals(expectedJson, responseEntity.getBody());

       verify(restTemplate, times(0)).exchange(eq(truProxyApiOfficersUrl + COMPANY_NUMBER), eq(org.springframework.http.HttpMethod.GET), any(), eq(OfficersResponse.class));
    }

    @Test
    public void testSearchCompanies_TruProxyApiError() throws Exception {
        String apiKey = "PwewCEztSW7XlaAKqkg4IaOsPelGynw6SN9WsbNf";

        // Mock RestTemplate to throw an exception for company URL
        Mockito.when(restTemplate.exchange(anyString(), eq(org.springframework.http.HttpMethod.GET), any(), eq(CompanySearchResponse.class)))
                .thenThrow(new RuntimeException("Mocked TruProxyAPI error"));


        companyService.searchCompanies(null, COMPANY_NUMBER, true, apiKey);
        
        verify(restTemplate, times(1)).exchange(anyString(), eq(org.springframework.http.HttpMethod.GET), any(), eq(CompanySearchResponse.class));
        verify(restTemplate, times(0)).exchange(anyString(), eq(org.springframework.http.HttpMethod.GET), any(), eq(OfficersResponse.class));
        verify(objectMapper, times(0)).writeValueAsString(any());
    }

    private static CompanySearchResult createCompanySearchResult() {
        CompanySearchResult companySearchResult = new CompanySearchResult();
        companySearchResult.setCompany_number(COMPANY_NUMBER);
        companySearchResult.setCompany_type("ltd");
        companySearchResult.setTitle(COMPANY_NAME);
        companySearchResult.setCompany_status("active");
        companySearchResult.setDate_of_creation("2008-02-11");
        return companySearchResult;
    }

    private SearchResponse createExpectedResponse(CompanySearchResult companySearchResult, List<Officer> officers) {
        SearchResponse searchResponse = new SearchResponse();
        searchResponse.setTotal_results(1);
        searchResponse.setItems(Collections.singletonList(companySearchResult));
        return searchResponse;
    }

    private static List<Officer> createActiveOfficers() {
        List<Officer> officers = new ArrayList<>();
        Officer officer1 = new Officer();
        officer1.setName("BOXALL, Sarah Victoria");
        officer1.setOfficer_role("secretary");
        officer1.setAppointed_on("2008-02-11");
        officer1.setResigned_on(null); // Set officer1 as active

        Officer officer2 = new Officer();
        officer2.setName("DOE, John");
        officer2.setOfficer_role("director");
        officer2.setAppointed_on("2010-01-01");
        officer2.setResigned_on("test"); // Set officer2 as inactive

        officers.add(officer1);
        officers.add(officer2);

        return officers;
    }
}
