package com.talenteo.hr.repository;

import com.talenteo.hr.model.entity.HumanResource;
import com.talenteo.hr.model.entity.VerificationToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VerificationTokenRepository extends JpaRepository<VerificationToken, Long> {
    VerificationToken findByConfirmationToken(String confirmationToken);

    VerificationToken findByhumanResource(HumanResource humanResource);
}
