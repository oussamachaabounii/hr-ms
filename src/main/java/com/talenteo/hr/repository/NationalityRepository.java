package com.talenteo.hr.repository;

import com.talenteo.hr.model.entity.Nationality;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NationalityRepository extends JpaRepository<Nationality, Long> {

    Nationality findByCountry(String country);
}
