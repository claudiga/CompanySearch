package org.risknarrative.companysearch.dto;

import lombok.Builder;

import java.util.List;

@Builder
public record CompanySearchResponse(Integer totalResults, List<CompanyDto> items) {

}

