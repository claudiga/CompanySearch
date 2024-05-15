package org.risknarrative.companysearch.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.risknarrative.companysearch.dto.CompanySearchRequest;
import org.risknarrative.companysearch.dto.CompanySearchResponse;
import org.risknarrative.companysearch.service.CompanyLookupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest()
public class CompanySearchControllerTest {

    private final ObjectMapper objectMapper = new ObjectMapper();
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private CompanyLookupService companyLookupService;

    @Test
    void testThatSearchCompanyReturns_200() throws Exception {
        CompanySearchRequest request = new CompanySearchRequest("test 1", "1234");
        CompanySearchResponse response = CompanySearchResponse
                .builder()
                .build();

        given(companyLookupService.findCompanies(request, false)).willReturn(response);

        mockMvc.perform(post("/search")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(response)));
        verify(companyLookupService, times(1)).findCompanies(eq(request), eq(false));
    }

    @Test
    void testThatSearchCompanyPassesActiveFlagFalse_WhenNoneProvided() throws Exception {
        CompanySearchRequest request = new CompanySearchRequest("test 1", "1234");
        CompanySearchResponse response = CompanySearchResponse
                .builder()
                .build();

        given(companyLookupService.findCompanies(request, false)).willReturn(response);

        mockMvc.perform(post("/search")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(response)));
        verify(companyLookupService, times(1)).findCompanies(eq(request), eq(false));

    }

    @Test
    void testThatSearchCompanyPassesActiveFlagTrue_WhenProvided() throws Exception {
        CompanySearchRequest request = new CompanySearchRequest("test 1", "1234");
        CompanySearchResponse response = CompanySearchResponse
                .builder()
                .build();

        given(companyLookupService.findCompanies(request, true)).willReturn(response);

        mockMvc.perform(post("/search")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .param("activeOnly", "true"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(response)));
        verify(companyLookupService, times(1)).findCompanies(eq(request), eq(true));

    }

    @Test
    void testThatSearchCompanyPassesActiveFlagFalse_WhenProvided() throws Exception {
        CompanySearchRequest request = new CompanySearchRequest("test 1", "1234");
        CompanySearchResponse response = CompanySearchResponse
                .builder()
                .build();

        given(companyLookupService.findCompanies(request, false)).willReturn(response);

        mockMvc.perform(post("/search")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .param("activeOnly", "false"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(response)));
        verify(companyLookupService, times(1)).findCompanies(eq(request), eq(false));

    }

}