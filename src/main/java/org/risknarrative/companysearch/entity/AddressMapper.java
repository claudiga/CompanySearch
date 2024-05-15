package org.risknarrative.companysearch.entity;

import org.risknarrative.companysearch.dto.AddressDto;
import org.springframework.stereotype.Component;

@Component
public class AddressMapper {

    public Address toAddressEntity(AddressDto addressDto) {
        return Address.builder()
                .addressLine1(addressDto.addressLine1())
                .postalCode(addressDto.postalCode())
                .premises(addressDto.premises())
                .locality(addressDto.locality())
                .country(addressDto.country())
                .build();
    }

    public AddressDto toAddressDto(Address address) {
        return AddressDto.builder()
                .country(address.getCountry())
                .addressLine1(address.getAddressLine1())
                .premises(address.getPremises())
                .postalCode(address.getPostalCode())
                .locality(address.getLocality())
                .build();

    }
}
