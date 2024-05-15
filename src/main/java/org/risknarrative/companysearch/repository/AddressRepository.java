package org.risknarrative.companysearch.repository;

import org.risknarrative.companysearch.entity.Address;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AddressRepository extends JpaRepository<Address, Integer> {

    Optional<Address> findByPremisesAndLocalityAndPostalCodeAndAddressLine1AndCountry(
            String premises, String locality, String postalCode, String addressLine1, String country);
}
