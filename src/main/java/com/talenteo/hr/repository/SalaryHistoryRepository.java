package com.talenteo.hr.repository;

import com.talenteo.hr.model.entity.Address;
import com.talenteo.hr.model.entity.SalaryHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SalaryHistoryRepository extends JpaRepository<SalaryHistory,Long> {

    List<SalaryHistory> findByHr_Id(Long hrId);
    SalaryHistory findTopByHr_IdOrderByStartDateDesc(Long hrId);
}
