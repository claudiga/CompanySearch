package org.risknarrative.companysearch.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Builder
@AllArgsConstructor
@RequiredArgsConstructor
@Entity
@Getter
@Table(name = "Address")
public class Address {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "address_id")
    private int addressId;

    @Column(name = "premises")
    private String premises;

    @Column(name = "locality")
    private String locality;

    @Column(name = "postal_code")
    private String postalCode;

    @Column(name = "address_line_1")
    private String addressLine1;

    @Column(name = "country")
    private String country;

}
