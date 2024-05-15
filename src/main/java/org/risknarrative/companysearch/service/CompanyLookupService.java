package org.risknarrative.companysearch.service;

import org.risknarrative.companysearch.dto.CompanySearchRequest;
import org.risknarrative.companysearch.dto.CompanySearchResponse;

public interface CompanyLookupService {

    CompanySearchResponse findCompanies(CompanySearchRequest companySearchRequest, boolean activeOnly);
}
