package org.risknarrative.companysearch.repository;

import org.risknarrative.companysearch.entity.Company;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CompanyRepository extends JpaRepository<Company, String> {
}