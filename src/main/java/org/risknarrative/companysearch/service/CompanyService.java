package org.risknarrative.companysearch.service;

import lombok.RequiredArgsConstructor;
import org.risknarrative.companysearch.dto.AddressDto;
import org.risknarrative.companysearch.dto.CompanyDto;
import org.risknarrative.companysearch.dto.CompanySearchResponse;
import org.risknarrative.companysearch.dto.OfficerDto;
import org.risknarrative.companysearch.entity.AddressMapper;
import org.risknarrative.companysearch.entity.CompanyMapper;
import org.risknarrative.companysearch.entity.OfficerMapper;
import org.risknarrative.companysearch.repository.AddressRepository;
import org.risknarrative.companysearch.repository.CompanyRepository;
import org.risknarrative.companysearch.repository.OfficerRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CompanyService {

    private final AddressMapper addressMapper;
    private final CompanyMapper companyMapper;

    private final OfficerMapper officerMapper;

    private final AddressRepository addressRepository;
    private final OfficerRepository officerRepository;
    private final CompanyRepository companyRepository;

    @Transactional
    public org.risknarrative.companysearch.entity.Address saveAddress(AddressDto addressDto) {
        org.risknarrative.companysearch.entity.Address address = addressMapper.toAddressEntity(addressDto);
        return addressRepository.save(address);

    }

    @Transactional
    public org.risknarrative.companysearch.entity.Company saveCompany(CompanyDto companyDto, org.risknarrative.companysearch.entity.Address address) {
        org.risknarrative.companysearch.entity.Company company = companyMapper.toCompanyEntity(companyDto, address);
        return companyRepository.save(company);

    }

    @Transactional
    public org.risknarrative.companysearch.entity.Officer saveOfficer(OfficerDto officerDto, org.risknarrative.companysearch.entity.Company company,
                                                                      org.risknarrative.companysearch.entity.Address address) {
        org.risknarrative.companysearch.entity.Officer officer = officerMapper.toOfficerEntity(officerDto, company, address);
        return officerRepository.save(officer);
    }

    @Transactional
    public Optional<org.risknarrative.companysearch.entity.Address> findExistingAddress(AddressDto addressDto) {
        return addressRepository.findByPremisesAndLocalityAndPostalCodeAndAddressLine1AndCountry(
                addressDto.premises(),
                addressDto.locality(),
                addressDto.postalCode(),
                addressDto.addressLine1(),
                addressDto.country()
        );
    }

    @Transactional
    public Optional<CompanySearchResponse> fetchFromDatabase(String companyNumber) {

        Optional<org.risknarrative.companysearch.entity.Company> companyRepositoryById = companyRepository.findById(companyNumber);
        if (companyRepositoryById.isEmpty()) {
            return Optional.empty();
        }
        CompanySearchResponse companySearchResponse = CompanySearchResponse.builder()
                .items(List.of(companyMapper.toCompanyDto(companyRepositoryById.get())))
                .totalResults(1)
                .build();
        return Optional.of(companySearchResponse);

    }
}
