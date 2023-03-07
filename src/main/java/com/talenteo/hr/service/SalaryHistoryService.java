package com.talenteo.hr.service;

import com.talenteo.hr.converter.SalaryHistoryConverter;
import com.talenteo.hr.converter.SalaryHistoryRequestConverter;
import com.talenteo.hr.dto.SalaryHistoryDto;
import com.talenteo.hr.dto.SalaryHistoryRequest;
import com.talenteo.hr.model.entity.SalaryHistory;
import com.talenteo.hr.repository.SalaryHistoryRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Component
@AllArgsConstructor
public class SalaryHistoryService {

    private final SalaryHistoryRepository salaryHistoryRepository;

    /**
     * ======================================================
     * Get history of salaries by Hr ID
     * ======================================================
     * @param hrId
     * @return
     */
    public List<SalaryHistoryDto> getAllByHrId(Long hrId) {
        List<SalaryHistory> salaryHistoryList = salaryHistoryRepository.findByHr_Id(hrId);
        return salaryHistoryList.stream().
                map(salaryHistory -> SalaryHistoryConverter.newInstance().convert(salaryHistory)).collect(Collectors.toList());

    }

    /**
     * ======================================================
     * Get latest Salary by Hr ID
     * ======================================================
     * @param hrId
     * @return
     */
    public Optional<SalaryHistoryDto> getlatestByHrId(Long hrId) {
        log.info("getlatestByHrId service resourceId"+hrId);
        Optional<SalaryHistory> latestSalary = Optional.ofNullable(salaryHistoryRepository.findTopByHr_IdOrderByStartDateDesc(hrId));
        return latestSalary.map(salhis -> SalaryHistoryConverter.newInstance().convert(salhis));
    }

    /**
     * ======================================================
     * Save salary history
     * ======================================================
     * @param salaryHistoryRequest
     * @return
     */
    public SalaryHistoryDto create(SalaryHistoryRequest salaryHistoryRequest) {

        SalaryHistory salaryHistory = SalaryHistoryRequestConverter.newInstance().convert(salaryHistoryRequest);
        SalaryHistory savedsSalaryHistory = salaryHistoryRepository.save(salaryHistory);

        return SalaryHistoryConverter.newInstance().convert(savedsSalaryHistory);
    }



}
