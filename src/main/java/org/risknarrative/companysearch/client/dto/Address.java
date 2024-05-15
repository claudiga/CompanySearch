package org.risknarrative.companysearch.client.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record Address(
        String premises,
        @JsonProperty("postal_code")

        String postalCode,
        String country,
        String locality,
        @JsonProperty("address_line_1")

        String addressLine1
) {
}