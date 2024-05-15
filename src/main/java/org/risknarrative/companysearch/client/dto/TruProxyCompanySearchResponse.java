package org.risknarrative.companysearch.client.dto;


import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record TruProxyCompanySearchResponse(
        List<Company> items
) {
    public record Company(
            @JsonProperty("company_status")

            String companyStatus,
            @JsonProperty("date_of_creation")

            String dateOfCreation,
            @JsonProperty("company_number")
            String companyNumber,
            String title,
            @JsonProperty("company_type")

            String companyType,
            Address address
    ) {
    }
}
