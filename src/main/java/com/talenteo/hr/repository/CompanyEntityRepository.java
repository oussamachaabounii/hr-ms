package com.talenteo.hr.repository;

import com.talenteo.hr.model.entity.Company;
import com.talenteo.hr.model.entity.CompanyEntity;
import com.talenteo.hr.model.entity.HumanResource;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CompanyEntityRepository extends JpaRepository<CompanyEntity,Long> {

    @Query("Select c from CompanyEntity c where c.company.id=:id AND c.isActive=true")
    List<CompanyEntity> findByCompanyId(Long id);
    @Query("SELECT c FROM CompanyEntity c where c.isActive=true  ")
    List<CompanyEntity> findAllCompanyEntities();
    Optional<CompanyEntity> findByLabel(String label);
}
