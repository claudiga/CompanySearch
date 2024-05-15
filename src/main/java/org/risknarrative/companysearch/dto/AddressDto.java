package org.risknarrative.companysearch.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

@Builder
public record AddressDto(
        String locality,
        @JsonProperty("postal_code")
        String postalCode,
        String premises,
        @JsonProperty("address_line_1")

        String addressLine1,
        String country
) {
}