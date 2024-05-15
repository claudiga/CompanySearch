package org.risknarrative.companysearch.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

@Builder
public record OfficerDto(
        String name,
        @JsonProperty("officer_role")
        String officerRole,
        @JsonProperty("appointed_on")

        String appointedOn,
        AddressDto address
) {
}
