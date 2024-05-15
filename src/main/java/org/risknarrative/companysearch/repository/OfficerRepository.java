package org.risknarrative.companysearch.repository;

import org.risknarrative.companysearch.entity.Officer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OfficerRepository extends JpaRepository<Officer, Integer> {
}