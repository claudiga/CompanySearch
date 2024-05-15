package org.risknarrative.companysearch.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.risknarrative.companysearch.dto.AddressDto;
import org.risknarrative.companysearch.dto.CompanyDto;
import org.risknarrative.companysearch.dto.CompanySearchResponse;
import org.risknarrative.companysearch.dto.OfficerDto;
import org.risknarrative.companysearch.entity.Address;
import org.risknarrative.companysearch.entity.AddressMapper;
import org.risknarrative.companysearch.entity.CompanyMapper;
import org.risknarrative.companysearch.entity.OfficerMapper;
import org.risknarrative.companysearch.repository.AddressRepository;
import org.risknarrative.companysearch.repository.CompanyRepository;
import org.risknarrative.companysearch.repository.OfficerRepository;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CompanyServiceTest {

    @Mock
    private AddressMapper addressMapper;

    @Mock
    private CompanyMapper companyMapper;

    @Mock
    private OfficerMapper officerMapper;

    @Mock
    private AddressRepository addressRepository;

    @Mock
    private CompanyRepository companyRepository;

    @Mock
    private OfficerRepository officerRepository;

    @InjectMocks
    private CompanyService companyService;

    @Test
    void testShouldSaveAndReturnAddress() {
        AddressDto addressDto =
                new AddressDto(
                        "tst street",
                        "s1 l",
                        "uk",
                        "uk",
                        "adrl 1");
        org.risknarrative.companysearch.entity.Address addressEntity = new org.risknarrative.companysearch.entity.Address();

        when(addressMapper.toAddressEntity(addressDto)).thenReturn(addressEntity);
        when(addressRepository.save(addressEntity)).thenReturn(addressEntity);

        org.risknarrative.companysearch.entity.Address savedAddress = companyService.saveAddress(addressDto);

        assertThat(savedAddress).isSameAs(addressEntity);
        verify(addressMapper).toAddressEntity(addressDto);
        verify(addressRepository).save(addressEntity);
    }

    @Test
    void testShouldSaveAndReturnCompany() {
        CompanyDto companyDto = CompanyDto.builder().build();
        org.risknarrative.companysearch.entity.Address addressEntity = new org.risknarrative.companysearch.entity.Address();
        org.risknarrative.companysearch.entity.Company companyEntity = new org.risknarrative.companysearch.entity.Company();

        when(companyMapper.toCompanyEntity(companyDto, addressEntity)).thenReturn(companyEntity);
        when(companyRepository.save(companyEntity)).thenReturn(companyEntity);

        org.risknarrative.companysearch.entity.Company savedCompany = companyService.saveCompany(companyDto, addressEntity);

        assertThat(savedCompany).isSameAs(companyEntity);
        verify(companyMapper).toCompanyEntity(companyDto, addressEntity);
        verify(companyRepository).save(companyEntity);
    }

    @Test
    void testShouldSaveAndReturnOfficer() {
        OfficerDto officerDto = OfficerDto.builder().build();
        org.risknarrative.companysearch.entity.Company companyEntity = new org.risknarrative.companysearch.entity.Company();
        org.risknarrative.companysearch.entity.Address addressEntity = new org.risknarrative.companysearch.entity.Address();
        org.risknarrative.companysearch.entity.Officer officerEntity = new org.risknarrative.companysearch.entity.Officer();

        when(officerMapper.toOfficerEntity(officerDto, companyEntity, addressEntity)).thenReturn(officerEntity);
        when(officerRepository.save(officerEntity)).thenReturn(officerEntity);

        org.risknarrative.companysearch.entity.Officer savedOfficer = companyService.saveOfficer(officerDto, companyEntity, addressEntity);

        assertThat(savedOfficer).isSameAs(officerEntity);
        verify(officerMapper).toOfficerEntity(officerDto, companyEntity, addressEntity);
        verify(officerRepository).save(officerEntity);
    }

    @Test
    void findExistingAddress_ShouldReturnExistingAddress_WhenAddressMatches() {
        AddressDto addressDto = AddressDto.builder()
                .country("uk")
                .postalCode("sl 1")
                .premises("premise")
                .locality("local")
                .addressLine1("adr l1")
                .build();
        Optional<Address> expectedAddress = Optional.of(new org.risknarrative.companysearch.entity.Address());

        when(addressRepository.findByPremisesAndLocalityAndPostalCodeAndAddressLine1AndCountry(
                addressDto.premises(),
                addressDto.locality(),
                addressDto.postalCode(),
                addressDto.addressLine1(),
                addressDto.country()
        )).thenReturn(expectedAddress);

        Optional<org.risknarrative.companysearch.entity.Address> actualAddress = companyService.findExistingAddress(addressDto);

        assertThat(actualAddress).isPresent();
        assertThat(actualAddress).isEqualTo(expectedAddress);
        verify(addressRepository).findByPremisesAndLocalityAndPostalCodeAndAddressLine1AndCountry(
                addressDto.premises(),
                addressDto.locality(),
                addressDto.postalCode(),
                addressDto.addressLine1(),
                addressDto.country()
        );
    }

    @Test
    void testShouldReturnEmpty_WhenNoAddressMatches() {
        AddressDto addressDto = AddressDto.builder()
                .country("uk")
                .postalCode("sl 1")
                .premises("premise")
                .locality("local")
                .addressLine1("adr l1")
                .build();

        when(addressRepository.findByPremisesAndLocalityAndPostalCodeAndAddressLine1AndCountry(
                addressDto.premises(),
                addressDto.locality(),
                addressDto.postalCode(),
                addressDto.addressLine1(),
                addressDto.country()
        )).thenReturn(Optional.empty());

        Optional<org.risknarrative.companysearch.entity.Address> actualAddress = companyService.findExistingAddress(addressDto);

        assertThat(actualAddress).isNotPresent();
        verify(addressRepository).findByPremisesAndLocalityAndPostalCodeAndAddressLine1AndCountry(
                addressDto.premises(),
                addressDto.locality(),
                addressDto.postalCode(),
                addressDto.addressLine1(),
                addressDto.country()
        );
    }

    @Test
    void testShouldReturnCompanySearchResponse_WhenCompanyIsFound() {
        String companyNumber = "123";
        org.risknarrative.companysearch.entity.Company companyEntity = new org.risknarrative.companysearch.entity.Company();
        CompanyDto dtoCompanyDto = CompanyDto.builder().build();

        when(companyRepository.findById(companyNumber)).thenReturn(Optional.of(companyEntity));

        when(companyMapper.toCompanyDto(companyEntity)).thenReturn(dtoCompanyDto);
        Optional<CompanySearchResponse> expectedResponse = Optional.of(CompanySearchResponse.builder()
                .items(List.of(dtoCompanyDto))
                .totalResults(1)
                .build());

        Optional<CompanySearchResponse> actualResponse = companyService.fetchFromDatabase(companyNumber);

        assertThat(actualResponse).isPresent();
        assertThat(actualResponse).isEqualTo(expectedResponse);
    }

    @Test
    void testShouldReturnEmpty_WhenNoCompanyIsFound() {
        String companyNumber = "123";
        when(companyRepository.findById(companyNumber)).thenReturn(Optional.empty());

        Optional<CompanySearchResponse> actualResponse = companyService.fetchFromDatabase(companyNumber);

        assertThat(actualResponse).isNotPresent();
    }


}
