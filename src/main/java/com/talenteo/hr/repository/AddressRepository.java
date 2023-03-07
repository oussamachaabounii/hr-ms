package com.talenteo.hr.repository;

import com.talenteo.hr.model.entity.Address;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AddressRepository extends JpaRepository<Address, Long> {
    Optional<Address> findByCountry(String label);

}
