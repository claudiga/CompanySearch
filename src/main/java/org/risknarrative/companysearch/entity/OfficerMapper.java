package org.risknarrative.companysearch.entity;

import lombok.RequiredArgsConstructor;
import org.risknarrative.companysearch.dto.OfficerDto;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OfficerMapper {

    private final AddressMapper addressMapper;

    public Officer toOfficerEntity(OfficerDto officerDto, Company company,
                                   Address address) {
        return Officer.builder()
                .officerRole(officerDto.officerRole())
                .appointedOn(officerDto.appointedOn())
                .officerAddress(address)
                .name(officerDto.name())
                .company(company)
                .build();

    }

    public OfficerDto toOfficerDto(Officer officer) {
        return OfficerDto.builder()
                .name(officer.getName())
                .appointedOn(officer.getAppointedOn())
                .officerRole(officer.getOfficerRole())
                .address(addressMapper.toAddressDto(officer.getOfficerAddress()))
                .build();
    }
}
