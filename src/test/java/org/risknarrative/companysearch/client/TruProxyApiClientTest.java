package org.risknarrative.companysearch.client;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.risknarrative.companysearch.client.dto.TruProxyCompanySearchResponse;
import org.risknarrative.companysearch.client.dto.TruProxyOfficersSearchResponse;
import org.risknarrative.companysearch.dto.CompanySearchRequest;
import org.risknarrative.companysearch.exception.ApplicationException;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class TruProxyApiClientTest {

    @Mock
    private RestTemplate restTemplate;

    private TruProxyApiClient truProxyApiClient;

    @BeforeEach
    public void setup() {
        truProxyApiClient = new TruProxyApiClient(
                restTemplate, "http://tru.company.com",
                "/companies/search",
                "/companies/officers/search",
                "dummy-api-key");
    }

    @Test
    void testSearchCompanyShouldReturnResults_WhenApiCallIsSuccessful() {
        CompanySearchRequest request = new CompanySearchRequest("001", "Example Corp");
        HttpHeaders headers = new HttpHeaders();
        headers.set("x-api-key", "dummy-api-key");
        HttpEntity<Object> entity = new HttpEntity<>(headers);

        TruProxyCompanySearchResponse mockResponse = new TruProxyCompanySearchResponse(null);
        ResponseEntity<TruProxyCompanySearchResponse> responseEntity = ResponseEntity.ok(mockResponse);

        when(restTemplate.exchange(
                eq("http://tru.company.com/companies/search"),
                eq(HttpMethod.GET),
                eq(entity),
                eq(TruProxyCompanySearchResponse.class),
                any(Map.class)
        )).thenReturn(responseEntity);

        TruProxyCompanySearchResponse result = truProxyApiClient.searchCompany(request);

        assertThat(result).isEqualTo(mockResponse);
    }

    @Test
    void testSearchCompanyShouldThrowApplicationException_WhenRestClientExceptionIsThrown() {
        CompanySearchRequest request = new CompanySearchRequest("001", null);
        HttpHeaders headers = new HttpHeaders();
        headers.set("x-api-key", "dummy-api-key");
        HttpEntity<Object> entity = new HttpEntity<>(headers);

        when(restTemplate.exchange(
                eq("http://tru.company.com/companies/search"),
                eq(HttpMethod.GET),
                eq(entity),
                eq(TruProxyCompanySearchResponse.class),
                any(Map.class)
        )).thenThrow(new RestClientException("Connection failed"));

        Throwable thrown = catchThrowable(() -> truProxyApiClient.searchCompany(request));

        assertThat(thrown).isInstanceOf(ApplicationException.class)
                .hasMessageContaining("An error occurred");
    }

    @Test
    void testSearchOfficersByCompanyNumberShouldReturnResults_WhenApiCallIsSuccessful() {
        String companyNumber = "001";
        HttpHeaders headers = new HttpHeaders();
        headers.set("x-api-key", "dummy-api-key");
        HttpEntity<Object> entity = new HttpEntity<>(headers);

        TruProxyOfficersSearchResponse mockResponse = new TruProxyOfficersSearchResponse(null);
        ResponseEntity<TruProxyOfficersSearchResponse> responseEntity = ResponseEntity.ok(mockResponse);

        when(restTemplate.exchange(
                eq("http://tru.company.com/companies/officers/search"),
                eq(HttpMethod.GET),
                eq(entity),
                eq(TruProxyOfficersSearchResponse.class),
                any(Map.class)
        )).thenReturn(responseEntity);

        TruProxyOfficersSearchResponse result = truProxyApiClient.searchOfficersByCompanyNumber(companyNumber);

        assertThat(result).isEqualTo(mockResponse);
    }


    @Test
    void testSearchOfficersByCompanyNumberShouldThrowApplicationException_WhenRestClientExceptionIsThrown() {
        String companyNumber = "001";
        HttpHeaders headers = new HttpHeaders();
        headers.set("x-api-key", "dummy-api-key");
        HttpEntity<Object> entity = new HttpEntity<>(headers);

        when(restTemplate.exchange(
                eq("http://tru.company.com/companies/officers/search"),
                eq(HttpMethod.GET),
                eq(entity),
                eq(TruProxyOfficersSearchResponse.class),
                any(Map.class)
        )).thenThrow(new RestClientException("Connection failed"));

        Throwable thrown = catchThrowable(() -> truProxyApiClient.searchOfficersByCompanyNumber(companyNumber));

        assertThat(thrown).isInstanceOf(ApplicationException.class)
                .hasMessageContaining("An error occurred");
    }

    @Test
    void testSearchCompanyShouldRemoveNonMatchingTitles_WhenCompanyNumberIsNotProvided() {
        CompanySearchRequest request = new CompanySearchRequest("test comp", null);

        HttpHeaders headers = new HttpHeaders();
        headers.set("x-api-key", "dummy-api-key");
        HttpEntity<Object> entity = new HttpEntity<>(headers);

        List<TruProxyCompanySearchResponse.Company> items = new ArrayList<>();
        items.add(new TruProxyCompanySearchResponse.Company("active", "28-12-2008", "1", "test comp", "ltd", null));
        items.add(new TruProxyCompanySearchResponse.Company("active", "28-12-2008", "1", "test non matching comp", "ltd", null));
        TruProxyCompanySearchResponse mockResponse = new TruProxyCompanySearchResponse(items);

        ResponseEntity<TruProxyCompanySearchResponse> responseEntity = ResponseEntity.ok(mockResponse);

        when(restTemplate.exchange(
                eq("http://tru.company.com/companies/search"),
                eq(HttpMethod.GET),
                eq(entity),
                eq(TruProxyCompanySearchResponse.class),
                any(Map.class)
        )).thenReturn(responseEntity);

        TruProxyCompanySearchResponse result = truProxyApiClient.searchCompany(request);

        assertThat(result.items()).hasSize(1);
        assertThat(result.items().get(0).title()).isEqualTo("test comp");
    }


}