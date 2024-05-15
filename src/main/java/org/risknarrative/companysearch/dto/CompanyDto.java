package org.risknarrative.companysearch.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

import java.util.List;

@Builder
public record CompanyDto(
        @JsonProperty("company_number")
        String companyNumber,
        @JsonProperty("company_type")
        String companyType,
        String title,
        @JsonProperty("company_status")

        String companyStatus,
        @JsonProperty("date_of_creation")

        String dateOfCreation,
        AddressDto address,
        List<OfficerDto> officers
) {
}