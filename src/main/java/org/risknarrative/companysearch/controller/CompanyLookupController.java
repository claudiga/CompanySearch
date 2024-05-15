package org.risknarrative.companysearch.controller;

import lombok.RequiredArgsConstructor;
import org.risknarrative.companysearch.dto.CompanySearchRequest;
import org.risknarrative.companysearch.dto.CompanySearchResponse;
import org.risknarrative.companysearch.service.CompanyLookupService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "/search")
@RequiredArgsConstructor
public class CompanyLookupController {

    private final CompanyLookupService companyLookupService;

    @PostMapping
    public CompanySearchResponse searchForCompany(@RequestBody CompanySearchRequest request,
                                                  @RequestParam(required = false, defaultValue = "false") boolean activeOnly) {
        return companyLookupService.findCompanies(request, activeOnly);
    }

}
