package org.risknarrative.companysearch.integration;

import com.github.tomakehurst.wiremock.client.WireMock;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.risknarrative.companysearch.dto.CompanyDto;
import org.risknarrative.companysearch.dto.CompanySearchRequest;
import org.risknarrative.companysearch.dto.CompanySearchResponse;
import org.risknarrative.companysearch.dto.OfficerDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.TestPropertySource;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.TestInstance.Lifecycle.PER_CLASS;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@AutoConfigureWireMock(port = 0)
@TestInstance(PER_CLASS)
@SpringBootTest(webEnvironment = RANDOM_PORT)
@TestPropertySource(properties = {
        "tru_proxy_apikey=testkey",
        "truproxy.api.companies.baseurl=http://localhost:${wiremock.server.port}/TruProxyAPI/rest/Companies/v1"
})
public class CompanyLookupIntegrationTest {

    @Autowired
    protected TestRestTemplate testRestTemplate;

    @LocalServerPort
    protected int port;

    @Value("classpath:CompanyLookupResponse.json")
    private Resource companyLookupResponse;
    @Value("classpath:OfficersLookupResponse.json")
    private Resource officersLookupResponse;

    @AfterEach
    public void cleanup() {
        WireMock.reset();
    }

    protected String getTestUrl(final String requestPath) {
        return String.format("http://localhost:%s/%s", port, requestPath);
    }

    @Test
    void testThatCompanyLookupServiceReturn200() throws IOException {
        stubFor(
                get(urlPathEqualTo("/TruProxyAPI/rest/Companies/v1/Search"))
                        .willReturn(okJson(Files.readString(Path.of(companyLookupResponse.getURI()))))
        );
        stubFor(
                get(urlPathEqualTo("/TruProxyAPI/rest/Companies/v1/Officers"))
                        .willReturn(okJson(Files.readString(Path.of(officersLookupResponse.getURI()))))
        );

        CompanySearchRequest companySearchRequest = new CompanySearchRequest("", "222");
        HttpEntity<CompanySearchRequest> companySearchRequestHttpEntity = new HttpEntity<>(companySearchRequest);

        ResponseEntity<CompanySearchResponse> response = testRestTemplate.exchange(
                getTestUrl("/search"),
                HttpMethod.POST,
                companySearchRequestHttpEntity,
                CompanySearchResponse.class
        );
        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().items()).isNotNull();
        assertThat(response.getBody().items()).areAtLeastOne(
                new Condition<CompanyDto>(company -> "12763638".equals(company.companyNumber()),
                        "should have company 12763638"));
        assertThat(response.getBody().items()).areAtLeastOne(
                new Condition<CompanyDto>(company -> "06500244".equals(company.companyNumber()),
                        "should have company 06500244"));
        assertThat(response.getBody().items().get(0).officers()).areAtLeastOne(
                new Condition<OfficerDto>(officer -> "BRAY, Simon Anton".equals(officer.name()),
                        "should have officer Bray"));
    }


    @Test
    void testThatOnlyActiveCompaniesReturnedWhenParameterIsTrue() throws IOException {
        stubFor(
                get(urlPathEqualTo("/TruProxyAPI/rest/Companies/v1/Search"))
                        .willReturn(okJson(Files.readString(Path.of(companyLookupResponse.getURI()))))
        );
        stubFor(
                get(urlPathEqualTo("/TruProxyAPI/rest/Companies/v1/Officers"))
                        .willReturn(okJson(Files.readString(Path.of(officersLookupResponse.getURI()))))
        );

        CompanySearchRequest companySearchRequest = new CompanySearchRequest("BBC LIMITED", "");
        HttpEntity<CompanySearchRequest> companySearchRequestHttpEntity = new HttpEntity<>(companySearchRequest);

        ResponseEntity<CompanySearchResponse> response = testRestTemplate.exchange(
                getTestUrl("/search?activeOnly=true"),
                HttpMethod.POST,
                companySearchRequestHttpEntity,
                CompanySearchResponse.class
        );
        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().items()).isNotNull();
        assertThat(response.getBody().totalResults()).isEqualTo(1);

        assertThat(response.getBody().items()).doNotHave(new Condition<CompanyDto>(
                company -> "06500241".equals(company.companyNumber()),
                "should not have inactive company"));

        assertThat(response.getBody().items()).have(
                new Condition<CompanyDto>(company -> "active".equals(company.companyStatus()),
                        "should only have active companies"));
    }

    @Test
    void testThatCompanyLookupServiceReturns500_whenTruProxyRequestFails() {
        stubFor(
                get(urlPathEqualTo("/TruProxyAPI/rest/Companies/v1/Search"))
                        .willReturn(serverError())
        );
        stubFor(
                get(urlPathEqualTo("/TruProxyAPI/rest/Companies/v1/Officers"))
                        .willReturn(serverError())
        );

        CompanySearchRequest companySearchRequest = new CompanySearchRequest("", "222");
        HttpEntity<CompanySearchRequest> companySearchRequestHttpEntity = new HttpEntity<>(companySearchRequest);

        ResponseEntity<CompanySearchResponse> response = testRestTemplate.exchange(
                getTestUrl("/search"),
                HttpMethod.POST,
                companySearchRequestHttpEntity,
                CompanySearchResponse.class
        );
        assertThat(response.getStatusCode().is5xxServerError()).isTrue();
    }

    @Test
    void testThatCompanyLookupServiceReturns400_whenRequestIsInvalid() {
        stubFor(
                get(urlPathEqualTo("/TruProxyAPI/rest/Companies/v1/Search"))
                        .willReturn(serverError())
        );
        stubFor(
                get(urlPathEqualTo("/TruProxyAPI/rest/Companies/v1/Officers"))
                        .willReturn(serverError())
        );

        CompanySearchRequest companySearchRequest = new CompanySearchRequest("", "");
        HttpEntity<CompanySearchRequest> companySearchRequestHttpEntity = new HttpEntity<>(companySearchRequest);

        ResponseEntity<CompanySearchResponse> response = testRestTemplate.exchange(
                getTestUrl("/search"),
                HttpMethod.POST,
                companySearchRequestHttpEntity,
                CompanySearchResponse.class
        );
        assertThat(response.getStatusCode().is4xxClientError()).isTrue();
    }

}
