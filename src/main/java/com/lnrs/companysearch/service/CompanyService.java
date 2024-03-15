package com.lnrs.companysearch.service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lnrs.companysearch.model.CompanySearchResponse;
import com.lnrs.companysearch.model.CompanySearchResult;
import com.lnrs.companysearch.model.Officer;
import com.lnrs.companysearch.model.OfficersResponse;
import com.lnrs.companysearch.model.SearchResponse;
import com.lnrs.companysearch.repository.CompanyRepository;

@Service
public class CompanyService {
	@Value("${truProxyApiCompanyUrl}")
	private String truProxyApiCompanyUrl;
	@Value("${truProxyApiOfficersUrl}")
	private String truProxyApiOfficersUrl;

	private final RestTemplate restTemplate;
	private final ObjectMapper objectMapper;
	private final CompanyRepository companyRepository;
	private boolean isCompanyNumberEmpty = false;
	SearchResponse response = new SearchResponse();
	private int total_results = 0;

	public CompanyService(RestTemplate restTemplate, ObjectMapper objectMapper, CompanyRepository companyRepository) {
		this.restTemplate = restTemplate;
		this.objectMapper = objectMapper;
		this.companyRepository = companyRepository;
	}

	public ResponseEntity<String> searchCompanies(String companyName, String companyNumber, boolean activeOnly, String apiKey) {
		total_results = 0;
		if (response.getItems() == null) {
			response.setItems(new ArrayList<>());
		}
		// call Database get company details when the request is in this format only { "companyNumber" : "1234567890" } 
		if (!activeOnly && companyName==null) {
			CompanySearchResult companySearchResult = companyRepository.searchCompanies(companyNumber);
			if (companySearchResult != null) {
				response.getItems().add(companySearchResult);
				response.setTotal_results(companySearchResult.getOfficers().size());
			}
		} else {
			ResponseEntity<CompanySearchResponse> companyResponseEntity = null;
			ResponseEntity<OfficersResponse> officersResponseEntity = null;
			String apiOfficersUrl;
			isCompanyNumberEmpty = companyNumber == null || companyNumber.isEmpty(); // Determine if the companyNumber is empty
			// Modify the URL based on companyNumber or companyName
			String apiCompanyUrl = truProxyApiCompanyUrl + (isCompanyNumberEmpty ? companyName : companyNumber);
			// Prepare headers with API key
			HttpHeaders headers = new HttpHeaders();
			headers.set("x-api-key", apiKey);
			HttpEntity<?> requestEntity = new HttpEntity<>(headers);
			try {
				// Make the HTTP GET request to the TruProxyAPI for company details
				companyResponseEntity = restTemplate.exchange(apiCompanyUrl, HttpMethod.GET, requestEntity, CompanySearchResponse.class);
				CompanySearchResponse companyResponse = companyResponseEntity.getBody();
				if (companyResponse != null && companyResponse.getItems() != null
						&& !companyResponse.getItems().isEmpty()) {
					List<CompanySearchResult> companies = companyResponse.getItems();
					for (CompanySearchResult company : companies) {
						if (activeOnly && "dissolved".equals(company.getCompany_status())) {
							continue; // Skip this company if activeOnly is true and company status is dissolved
						}
						List<Officer> officers = new ArrayList<>();
						// Build the TruProxyAPI URLs and Make the HTTP GET request to the TruProxyAPI for officers details
						apiOfficersUrl = truProxyApiOfficersUrl + (isCompanyNumberEmpty ? company.getCompany_number() : companyNumber);
						officersResponseEntity = restTemplate.exchange(apiOfficersUrl, HttpMethod.GET, requestEntity, OfficersResponse.class);

						if (officersResponseEntity != null) {
							OfficersResponse officersResponse = officersResponseEntity.getBody();
							// Filter out the officers where resigned_on is not null
							if (officersResponse.getItems() != null) {
								officers = officersResponseEntity.getBody().getItems().stream()
										.filter(officer -> officer.getResigned_on() == null)
										.collect(Collectors.toList());
							}
						}
						CompanySearchResult companySearchResult = getCompanySearchResult(company, officers);
						response.getItems().add(companySearchResult);
						if (!isCompanyNumberEmpty) {
							break;
						}
					}
					response.setTotal_results(total_results);
				}
			} catch (Exception e) {
				return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
						.body("Error occurred while processing the request.");
			}
		}
		String jsonResponse = formatResponse();
		return ResponseEntity.ok(jsonResponse);
	}

	private String formatResponse() {
		// Convert the final response model to JSON format
		String jsonResponse;
		try {
			jsonResponse = objectMapper.writeValueAsString(response);
		} catch (Exception e) {
			jsonResponse = "{\"error\": \"Error occurred while processing response.\"}";
		}
		return jsonResponse;
	}

	private CompanySearchResult getCompanySearchResult(CompanySearchResult companySearchResult,
			List<Officer> officers) {
		// Filter out the officers where resigned_on is not null
		if (officers != null) {
			officers = officers.stream().filter(officer -> officer.getResigned_on() == null)
					.collect(Collectors.toList());
		}
		companySearchResult.setOfficers(officers);
		total_results += officers.size();
		return companySearchResult;
	}
}