package org.risknarrative.companysearch.entity;

import lombok.RequiredArgsConstructor;
import org.risknarrative.companysearch.dto.CompanyDto;
import org.risknarrative.companysearch.dto.OfficerDto;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class CompanyMapper {

    private final AddressMapper addressMapper;
    private final OfficerMapper officerMapper;

    public Company toCompanyEntity(CompanyDto companyDto, Address address) {

        return Company.builder()
                .address(address)
                .companyNumber(companyDto.companyNumber())
                .companyStatus(companyDto.companyStatus())
                .title(companyDto.title())
                .dateOfCreation(companyDto.dateOfCreation())
                .companyType(companyDto.companyType())
                .build();

    }

    public CompanyDto toCompanyDto(Company company) {
        List<OfficerDto> officerDtoList = company.getOfficers().stream().map(officerMapper::toOfficerDto).collect(Collectors.toList());

        return CompanyDto.builder()
                .officers(officerDtoList)
                .address(addressMapper.toAddressDto(company.getAddress()))
                .companyNumber(company.getCompanyNumber())
                .companyStatus(company.getCompanyStatus())
                .companyType(company.getCompanyType())
                .title(company.getTitle())
                .dateOfCreation(company.getDateOfCreation())
                .build();
    }
}
