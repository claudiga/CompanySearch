package org.risknarrative.companysearch.service;

import lombok.RequiredArgsConstructor;
import org.risknarrative.companysearch.client.TruProxyApiClient;
import org.risknarrative.companysearch.client.dto.TruProxyCompanySearchResponse;
import org.risknarrative.companysearch.client.dto.TruProxyOfficersSearchResponse;
import org.risknarrative.companysearch.dto.*;
import org.risknarrative.companysearch.exception.ApplicationUserException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TrueProxyApiCompanyLookupService implements CompanyLookupService {

    private final CompanyService companyService;

    private final TruProxyApiClient truProxyApiClient;

    @Transactional
    public CompanySearchResponse findCompanies(CompanySearchRequest companySearchRequest, boolean activeOnly) {
        validateRequest(companySearchRequest);
        if (StringUtils.hasLength(companySearchRequest.companyNumber())) {
            Optional<CompanySearchResponse> companySearchResponse = companyService.fetchFromDatabase(companySearchRequest.companyNumber());
            if (companySearchResponse.isPresent()) {
                CompanySearchResponse response = companySearchResponse.get();
                if (activeOnly) {
                    removeIfNotActive(response);
                    response = new CompanySearchResponse(response.items().size(), response.items());
                }
                return response;
            }
        }
        TruProxyCompanySearchResponse truProxyCompanySearchResponse = truProxyApiClient.searchCompany(companySearchRequest);
        if (activeOnly && truProxyCompanySearchResponse != null) {
            removeIfNotActive(truProxyCompanySearchResponse);
        }
        CompanySearchResponse companySearchResponse = buildResponse(truProxyCompanySearchResponse);
        decorateWithOfficers(companySearchResponse);
        if (StringUtils.hasLength(companySearchRequest.companyNumber())) {
            saveCompanyAndOfficers(companySearchResponse);
        }
        return companySearchResponse;
    }

    private void removeIfNotActive(TruProxyCompanySearchResponse body) {
        if (body.items() != null) {
            body.items().removeIf(company -> !"active".equalsIgnoreCase(company.companyStatus()));
        }
    }


    private void removeIfNotActive(CompanySearchResponse response) {
        if (response.items() != null) {
            response.items().removeIf(company -> !"active".equalsIgnoreCase(company.companyStatus()));
        }
    }

    private void saveCompanyAndOfficers(CompanySearchResponse companySearchResponse) {

        companySearchResponse.items().forEach(company -> {
            Optional<org.risknarrative.companysearch.entity.Address> existingAddress = companyService.findExistingAddress(company.address());
            org.risknarrative.companysearch.entity.Address address;
            address = existingAddress.orElseGet(() -> companyService.saveAddress(company.address()));

            org.risknarrative.companysearch.entity.Company companyEntity = companyService.saveCompany(company, address);

            company.officers().forEach(officer -> {
                Optional<org.risknarrative.companysearch.entity.Address> existingOfficerAddressOptional = companyService.findExistingAddress(officer.address());
                org.risknarrative.companysearch.entity.Address officerAddress;
                officerAddress = existingOfficerAddressOptional.orElseGet(() -> companyService.saveAddress(officer.address()));
                companyService.saveOfficer(officer, companyEntity, officerAddress);
            });
        });
    }


    private void decorateWithOfficers(CompanySearchResponse companySearchResponse) {
        companySearchResponse.items().forEach(company -> {
            TruProxyOfficersSearchResponse officersSearchResponse = truProxyApiClient.searchOfficersByCompanyNumber(company.companyNumber());
            if (officersSearchResponse == null || officersSearchResponse.items() == null) {
                return;
            }
            officersSearchResponse.items().removeIf(officer -> officer.resignedOn() != null);

            officersSearchResponse.items().forEach(officer -> {

                OfficerDto mappedOfficerDto = OfficerDto.builder()
                        .address(AddressDto.builder()
                                .locality(officer.address().locality())
                                .premises(officer.address().premises())
                                .postalCode(officer.address().postalCode())
                                .addressLine1(officer.address().addressLine1())
                                .country(officer.address().country())
                                .build())
                        .officerRole(officer.officerRole())
                        .appointedOn(officer.appointedOn())
                        .name(officer.name())
                        .build();
                company.officers().add(mappedOfficerDto);

            });
        });
    }

    private CompanySearchResponse buildResponse(TruProxyCompanySearchResponse tpResponse) {
        if (tpResponse == null || tpResponse.items() == null) {
            return CompanySearchResponse.builder()
                    .items(new ArrayList<>())
                    .totalResults(0)
                    .build();
        }
        List<CompanyDto> companies = tpResponse.items().stream().map(company ->
                CompanyDto.builder()
                        .companyNumber(company.companyNumber())
                        .companyStatus(company.companyStatus())
                        .companyType(company.companyType())
                        .title(company.title())
                        .dateOfCreation(company.dateOfCreation())
                        .officers(new ArrayList<>())
                        .address(AddressDto.builder()
                                .addressLine1(company.address().addressLine1())
                                .country(company.address().country())
                                .postalCode(company.address().postalCode())
                                .premises(company.address().premises())
                                .locality(company.address().locality())
                                .build())
                        .build()

        ).collect(Collectors.toList());
        return CompanySearchResponse.builder()
                .items(companies)
                .totalResults(tpResponse.items().size())
                .build();
    }

    private void validateRequest(CompanySearchRequest companySearchRequest) {
        if (!StringUtils.hasLength(companySearchRequest.companyNumber()) && !StringUtils.hasLength(companySearchRequest.companyName())) {
            throw new ApplicationUserException("Both companyNumber and companyName cannot be null");
        }
    }
}
