package org.risknarrative.companysearch.client.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record TruProxyOfficersSearchResponse(
        List<Officer> items
) {
    public record Officer(
            Address address,
            String name,
            @JsonProperty("appointed_on")

            String appointedOn,

            @JsonProperty("resigned_on")

            String resignedOn,

            @JsonProperty("officer_role")
            String officerRole,
            OfficerLinks links,
            DateOfBirth dateOfBirth,
            String occupation,
            String countryOfResidence,
            String nationality
    ) {
        public record OfficerLinks(
                OfficerAppointments appointments
        ) {
            public record OfficerAppointments(
                    String appointments
            ) {
            }
        }

        public record DateOfBirth(
                int month,
                int year
        ) {
        }
    }
}
