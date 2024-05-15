package org.risknarrative.companysearch.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Builder
@AllArgsConstructor
@RequiredArgsConstructor
@Getter
@Entity
@Table(name = "Officer")
public class Officer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "officer_id")
    private int officerId;

    @ManyToOne
    @JoinColumn(name = "company_number", referencedColumnName = "company_number")
    private Company company;


    @Column(name = "name")
    private String name;

    @Column(name = "officer_role")
    private String officerRole;

    @Column(name = "appointed_on")
    private String appointedOn;

    @ManyToOne
    @JoinColumn(name = "officer_address_id")
    private Address officerAddress;

    @Column(name = "officer_address_id", insertable = false, updatable = false)
    private int officerAddressId;

    // getters and setters
}

