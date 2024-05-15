package org.risknarrative.companysearch.client;

import org.risknarrative.companysearch.client.dto.TruProxyCompanySearchResponse;
import org.risknarrative.companysearch.client.dto.TruProxyOfficersSearchResponse;
import org.risknarrative.companysearch.dto.CompanySearchRequest;
import org.risknarrative.companysearch.exception.ApplicationException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Service
public class TruProxyApiClient {

    private final RestTemplate restTemplate;
    private final String baseUrl;
    private final String searchPath;
    private final String searchOfficersPath;
    private final String apiKey;

    public TruProxyApiClient(RestTemplate restTemplate,
                             @Value("${truproxy.api.companies.baseurl}") String baseUrl,
                             @Value("${truproxy.api.companies.search.path}") String searchPath,
                             @Value("${truproxy.api.companies.officers.path}") String searchOfficersPath,
                             @Value("${truproxy.api.apikey}") String apiKey) {
        this.restTemplate = restTemplate;
        this.baseUrl = baseUrl;
        this.searchPath = searchPath;
        this.searchOfficersPath = searchOfficersPath;
        this.apiKey = apiKey;
    }

    public TruProxyCompanySearchResponse searchCompany(CompanySearchRequest companySearchRequest) {
        String searchTerm;
        if (StringUtils.hasLength(companySearchRequest.companyNumber())) {
            searchTerm = companySearchRequest.companyNumber();
        } else {
            searchTerm = companySearchRequest.companyName();
        }

        HttpHeaders httpHeaders = createHttpHeadersWithApiKey();
        HttpEntity<Object> entity = new HttpEntity<>(httpHeaders);

        Map<String, String> params = new HashMap<>();
        params.put("searchTerm", searchTerm);
        ResponseEntity<TruProxyCompanySearchResponse> companySearchResponseResponseEntity;
        try {
            companySearchResponseResponseEntity = restTemplate.exchange(
                    baseUrl + searchPath,
                    HttpMethod.GET,
                    entity,
                    TruProxyCompanySearchResponse.class,
                    params);

        } catch (RestClientException restClientException) {
            restClientException.printStackTrace();
            throw new ApplicationException("An error occurred");
        }

        TruProxyCompanySearchResponse companySearchResponse = companySearchResponseResponseEntity.getBody();
        if (!StringUtils.hasLength(companySearchRequest.companyNumber())) {
            removeIfTitleNotMatchSearchTerm(searchTerm, Objects.requireNonNull(companySearchResponse));
        }

        return companySearchResponse;
    }

    private HttpHeaders createHttpHeadersWithApiKey() {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.set("x-api-key", apiKey);
        return httpHeaders;
    }

    public TruProxyOfficersSearchResponse searchOfficersByCompanyNumber(String companyNumber) {
        HttpHeaders httpHeaders = createHttpHeadersWithApiKey();
        HttpEntity<Object> entity = new HttpEntity<>(httpHeaders);

        Map<String, String> params = new HashMap<>();
        params.put("number", companyNumber);
        ResponseEntity<TruProxyOfficersSearchResponse> officersResponseEntity;
        try {
            officersResponseEntity = restTemplate.exchange(
                    baseUrl + searchOfficersPath,
                    HttpMethod.GET,
                    entity,
                    TruProxyOfficersSearchResponse.class,
                    params);

        } catch (RestClientException restClientException) {
            restClientException.printStackTrace();
            throw new ApplicationException("An error occurred");
        }

        return officersResponseEntity.getBody();
    }

    private void removeIfTitleNotMatchSearchTerm(
            String searchTerm,
            TruProxyCompanySearchResponse companySearchResponse) {


        if (companySearchResponse.items() == null) {
            return;
        }

        companySearchResponse.items().removeIf(
                item -> !searchTerm.equalsIgnoreCase(item.title()));

    }
}
