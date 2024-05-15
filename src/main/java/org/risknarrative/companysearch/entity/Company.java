package org.risknarrative.companysearch.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Builder
@AllArgsConstructor
@RequiredArgsConstructor
@Entity
@Getter
@Table(name = "Company")
public class Company {

    @Id
    @Column(name = "company_number")
    private String companyNumber;

    @Column(name = "company_type")
    private String companyType;

    @Column(name = "title")
    private String title;

    @Column(name = "company_status")
    private String companyStatus;

    @Column(name = "date_of_creation")
    private String dateOfCreation;

    @ManyToOne
    @JoinColumn(name = "address_id")
    private Address address;

    @OneToMany(mappedBy = "company", cascade = CascadeType.ALL)
    private List<Officer> officers;

}
