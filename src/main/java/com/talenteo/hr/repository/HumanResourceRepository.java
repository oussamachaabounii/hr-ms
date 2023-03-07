package com.talenteo.hr.repository;

import com.talenteo.hr.dto.Role;
import com.talenteo.hr.model.entity.Address;
import com.talenteo.hr.model.entity.Company;
import com.talenteo.hr.model.entity.CompanyEntity;
import com.talenteo.hr.model.entity.HumanResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface HumanResourceRepository extends JpaRepository<HumanResource, Long> {

    Optional<HumanResource> findByEmail(String email);

    List<HumanResource> findByRole(Role role);

    @Query("Select h from HumanResource h where h.role= 'Consultant' AND h.companyEntity=:companyEntity")
    List<HumanResource> findByCompanyEntity(CompanyEntity companyEntity);
    @Query("Select h from HumanResource h inner join CompanyEntity c on c.id=h.companyEntity where h.role= 'Manager' AND h.isActive=true  AND c.company=:company OR h.role= 'Recruiter'   AND c.company=:company")
    List<HumanResource> findByCompanyEntityId(Company company);

    @Query("Select h from HumanResource h where h.supervisor=:supervisor AND h.isActive=true")
    List<HumanResource> findBySupervisor(HumanResource supervisor);

    @Query("Select h from HumanResource h where h.supervisor.id=:id AND h.isActive=true")
    List<HumanResource> findBySupervisorId(Long id);

    @Query("SELECT h FROM HumanResource h WHERE h.firstname LIKE CONCAT('%',:modele,'%') ")
    List<HumanResource> selectFirstname(String modele);

    @Query("SELECT h FROM HumanResource h where h.supervisor.id=:id  AND h.isActive=true AND(h.firstname LIKE CONCAT('%',:query,'%') OR h.lastname LIKE CONCAT('%',:query,'%') OR h.email LIKE CONCAT('%',:query,'%'))")
    List<HumanResource> findByCriteria(Long id, String query);

    @Query("Select h from HumanResource h where h.companyEntity.company.id=:company_id")
    List<HumanResource> findByCompanyId(Long company_id);

    @Query("Select h from HumanResource h where h.companyEntity.company.label=:label AND h.isActive=true")
    List<HumanResource> findByCompanyLabel(String label);

    Page<HumanResource> findByRole(Role role, Pageable pageable);

    @Query("Select h from HumanResource h where h.companyEntity.company.id=:companyId AND h.role = 'Company_Admin' AND h.isActive=true")
    Optional<HumanResource> getCompanyAdminByCompany(Long companyId);
    @Query("Select h from HumanResource h where h.companyEntity=:id AND h.role = 'Company_Admin'")
    List<HumanResource> findByCompanyEntity(Long id);
    @Query("Select h from HumanResource h where h.supervisor=:supervisor AND h.role = 'Manager'")
    List<HumanResource> findBySupervisorManager(HumanResource supervisor);
    @Query("Select h from HumanResource h where h.companyEntity.company.id=:companyId AND h.role = 'Manager' AND h.isActive=true")
    List<HumanResource> getManagerByCompany(Long companyId);
    @Query("Select h from HumanResource h where h.companyEntity.company.id=:companyId AND h.role = 'Consultant' AND h.isActive=true")
    List<HumanResource> getConsultantByCompany(Long companyId);
}
