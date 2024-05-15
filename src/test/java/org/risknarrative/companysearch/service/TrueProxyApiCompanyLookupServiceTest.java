package org.risknarrative.companysearch.service;

import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.risknarrative.companysearch.client.TruProxyApiClient;
import org.risknarrative.companysearch.client.dto.TruProxyCompanySearchResponse;
import org.risknarrative.companysearch.client.dto.TruProxyOfficersSearchResponse;
import org.risknarrative.companysearch.dto.AddressDto;
import org.risknarrative.companysearch.dto.CompanyDto;
import org.risknarrative.companysearch.dto.CompanySearchRequest;
import org.risknarrative.companysearch.dto.CompanySearchResponse;
import org.risknarrative.companysearch.exception.ApplicationUserException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TrueProxyApiCompanyLookupServiceTest {

    @Mock
    private CompanyService companyService;

    @Mock
    private TruProxyApiClient truProxyApiClient;

    @InjectMocks
    private TrueProxyApiCompanyLookupService service;

    @Test
    void testShouldFetchFromDatabase_WhenCompanyNumberIsPresent() {
        CompanySearchRequest request = new CompanySearchRequest(null, "1234");
        CompanySearchResponse mockResponse = createCompanySearchResponse("active");

        when(companyService.fetchFromDatabase("1234")).thenReturn(Optional.of(mockResponse));

        CompanySearchResponse result = service.findCompanies(request, false);

        assertThat(result).isEqualTo(mockResponse);
        verify(truProxyApiClient, never()).searchCompany(any());
    }

    @Test
    void testShouldFetchFromApi_WhenDatabaseHasNoRecord() {
        CompanySearchRequest request = new CompanySearchRequest(null, "1234");
        TruProxyCompanySearchResponse apiResponse = createClientSearchResponse();

        when(companyService.fetchFromDatabase("1234")).thenReturn(Optional.empty());
        when(truProxyApiClient.searchCompany(request)).thenReturn(apiResponse);

        CompanySearchResponse result = service.findCompanies(request, true);

        assertThat(result).isNotNull();
        verify(companyService, times(1)).fetchFromDatabase("1234");
    }

    @Test
    void testShouldOnlyReturnActiveCompaniesFromDatabase_WhenActiveOnlyIsTrue() {
        CompanySearchRequest request = new CompanySearchRequest(null, "1234");
        CompanySearchResponse mockResponse = createCompanySearchResponse("dissolved");

        CompanyDto activeCompanyDto = CompanyDto.builder()
                .companyNumber("123").title("active Company")
                .companyStatus("active").companyType("ltd")
                .address(AddressDto.builder()
                        .postalCode("MT1 1LL")
                        .country("UK")
                        .build())
                .build();

        CompanyDto inactiveCompanyDto = CompanyDto.builder()
                .companyNumber("123").title("non active Company")
                .companyStatus("not active").companyType("ltd")
                .address(AddressDto.builder()
                        .postalCode("MT1 1LL")
                        .country("UK")
                        .build())
                .build();
        mockResponse.items().add(activeCompanyDto);
        mockResponse.items().add(inactiveCompanyDto);

        when(companyService.fetchFromDatabase("1234")).thenReturn(Optional.of(mockResponse));
        CompanySearchResponse result = service.findCompanies(request, true);
        assertThat(result).isNotNull();
        assertThat(result.items()).isNotNull();
        assertThat(result.totalResults()).isEqualTo(1);
        assertThat(result.items()).doNotHave(new Condition<CompanyDto>(
                company -> "non active Company".equals(company.companyStatus()),
                "should not have inactive company"));
        assertThat(result.items()).have(new Condition<CompanyDto>(
                company -> "active".equals(company.companyStatus()),
                "should all be active companies"));
    }

    @Test
    void testShouldOnlyReturnActiveCompanies_WhenActiveOnlyIsTrue() {
        CompanySearchRequest request = new CompanySearchRequest("Test", null);

        TruProxyCompanySearchResponse apiResponse = new TruProxyCompanySearchResponse(new ArrayList<>(List.of(
                createCompany("active", "active1"),
                createCompany("active", "active2"),
                createCompany("dissolved", "test"),
                createCompany("dissolved", "test2")
        )));

        when(truProxyApiClient.searchCompany(request)).thenReturn(apiResponse);
        CompanySearchResponse result = service.findCompanies(request, true);
        assertThat(result).isNotNull();
        assertThat(result.items()).isNotNull();
        assertThat(result.totalResults()).isEqualTo(2);
        assertThat(result.items()).doNotHave(new Condition<CompanyDto>(
                company -> "dissolved".equals(company.companyStatus()),
                "should not have inactive company"));
        assertThat(result.items()).have(new Condition<CompanyDto>(
                company -> "active".equals(company.companyStatus()),
                "should all be active companies"));
    }


    @Test
    void testShouldSaveCompanyAndOfficer_WhenCompanyNumberProvided() {
        CompanySearchRequest request = new CompanySearchRequest(null, "123");

        TruProxyCompanySearchResponse apiResponse = new TruProxyCompanySearchResponse(new ArrayList<>(List.of(
                createCompany("active", "active1")
        )));
        TruProxyOfficersSearchResponse officerResponse = createOfficerResponse();

        when(truProxyApiClient.searchCompany(request)).thenReturn(apiResponse);
        when(truProxyApiClient.searchOfficersByCompanyNumber("123")).thenReturn(officerResponse);
        CompanySearchResponse result = service.findCompanies(request, true);

        verify(companyService, times(1)).saveCompany(any(), any());
        verify(companyService, times(1)).saveOfficer(any(), any(), any());
        verify(companyService, times(2)).saveAddress(any());
    }

    @Test
    void testShouldReuseAddress_WhenAddressAlreadyExists() {
        CompanySearchRequest request = new CompanySearchRequest(null, "123");

        TruProxyCompanySearchResponse apiResponse = new TruProxyCompanySearchResponse(new ArrayList<>(List.of(
                createCompany("active", "active1")
        )));
        TruProxyOfficersSearchResponse officerResponse = createOfficerResponse();

        when(truProxyApiClient.searchCompany(request)).thenReturn(apiResponse);
        when(truProxyApiClient.searchOfficersByCompanyNumber("123")).thenReturn(officerResponse);
        when(companyService.findExistingAddress(
                any())).thenReturn(Optional.of(org.risknarrative.companysearch.entity.Address.builder().build()));
        service.findCompanies(request, true);

        verify(companyService, times(0)).saveAddress(any());
    }

    @Test
    public void testShouldReturnEmptyResult_whenResponseNull() {
        CompanySearchRequest request = new CompanySearchRequest(null, "123");


        when(truProxyApiClient.searchCompany(request)).thenReturn(null);
        CompanySearchResponse result = service.findCompanies(request, true);

        assertThat(result.totalResults()).isEqualTo(0);
        assertThat(result.items().size()).isEqualTo(0);
    }

    @Test
    void testShouldThrowException_WhenRequestInvalid() {
        CompanySearchRequest request = new CompanySearchRequest(null, null);

        assertThatThrownBy(() -> service.findCompanies(request, false))
                .isInstanceOf(ApplicationUserException.class)
                .hasMessageContaining("Both companyNumber and companyName cannot be null");
    }

    private TruProxyOfficersSearchResponse createOfficerResponse() {
        return new TruProxyOfficersSearchResponse(new ArrayList<>(List.of(
                new TruProxyOfficersSearchResponse.Officer(
                        new org.risknarrative.companysearch.client.dto.Address
                                ("office", "sl 1", "uk", "l", "al1"),
                        "officer 1", "2008-02-11", null, "director",
                        null, null, null, "uk", "british"))));
    }

    private TruProxyCompanySearchResponse createClientSearchResponse() {
        return new TruProxyCompanySearchResponse(
                new ArrayList<>(List.of(createCompany("active", "test ltd"))));
    }

    private TruProxyCompanySearchResponse.Company createCompany(String status, String title) {
        return new TruProxyCompanySearchResponse.Company(status, "2008-02-11",
                "123", title, "ltd", new org.risknarrative.companysearch.client.dto.Address("Retford",
                "r1", "123", "tst rd", "uk"));
    }

    private CompanySearchResponse createCompanySearchResponse(String status) {
        return CompanySearchResponse.builder()
                .totalResults(1)
                .items(new ArrayList<>(List.of(
                        CompanyDto.builder()
                                .companyNumber("123")
                                .title("Test Company")
                                .companyStatus(status)
                                .companyType("ltd")
                                .address(AddressDto.builder()
                                        .postalCode("MT1 1LL")
                                        .country("UK")
                                        .build())
                                .build()

                )))
                .build();

    }
}
