package com.talenteo.hr.repository;

import com.talenteo.hr.model.entity.Company;
import com.talenteo.hr.model.entity.HumanResource;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CompanyRepository extends JpaRepository<Company,Long> {
    Optional<Company> findByLabel(String label);
    @Query("SELECT c FROM Company c where c.isActive=true ")
    List<Company> findAllCompanies();
    @Query("SELECT c FROM Company c where c.label LIKE CONCAT('%',:query,'%')")
    List<Company> findByKeyword(String query);

}
