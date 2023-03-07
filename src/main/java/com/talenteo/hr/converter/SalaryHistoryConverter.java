package com.talenteo.hr.converter;

import com.talenteo.hr.dto.SalaryHistoryDto;
import com.talenteo.hr.model.entity.SalaryHistory;
import lombok.Data;
import org.springframework.core.convert.converter.Converter;

import java.util.Objects;

@Data(staticConstructor = "newInstance")
public class SalaryHistoryConverter implements Converter<SalaryHistory, SalaryHistoryDto> {

    @Override
    public SalaryHistoryDto convert(SalaryHistory salaryHistory) {
        if (Objects.isNull(salaryHistory)) {
            return null;
        }
        return SalaryHistoryDto.builder().
                id(salaryHistory.getId())
                .currency(salaryHistory.getCurrency())
                .startDate(salaryHistory.getStartDate())
                .endDate(salaryHistory.getEndDate())
                .salaryAmount(salaryHistory.getSalaryAmount())
                .hrId(salaryHistory.getHr().getId())
                .build();
    }
}
