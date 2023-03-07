package com.talenteo.hr.service;

import com.talenteo.hr.HrMsApplication;
import com.talenteo.hr.dto.SalaryHistoryDto;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.inject.Inject;
import java.util.List;
import java.util.Optional;

@Slf4j
@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@SpringBootTest(classes = HrMsApplication.class)
public class SalaryHistoryServiceTest {


    @Inject
    private SalaryHistoryService salaryHistoryService;

    @Test
    public void should_return_latest_salary__by_hr_id() {

        Optional<SalaryHistoryDto> salaryHistoryDto = salaryHistoryService.getlatestByHrId(2L);
        Assertions.assertThat(salaryHistoryDto).isNotNull();
        Assertions.assertThat(salaryHistoryDto.get().getCurrency()).isEqualTo("EUR");

    }

    @Test
    public void should_return_salary_history_by_hr_id() {

        List<SalaryHistoryDto> salaryHistoryDto = salaryHistoryService.getAllByHrId(2L);
        Assertions.assertThat(salaryHistoryDto).isNotNull();
        Assertions.assertThat(salaryHistoryDto.size()).isEqualTo(1);

    }
}
