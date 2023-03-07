package com.talenteo.hr.converter;

import com.talenteo.hr.dto.SalaryHistoryRequest;
import com.talenteo.hr.model.entity.HumanResource;
import com.talenteo.hr.model.entity.SalaryHistory;
import lombok.Data;
import org.springframework.core.convert.converter.Converter;

import java.util.Objects;

@Data(staticConstructor = "newInstance")
public class SalaryHistoryRequestConverter implements Converter<SalaryHistoryRequest, SalaryHistory> {

    @Override
    public SalaryHistory convert(SalaryHistoryRequest salaryHistoryRequest) {
        if (Objects.isNull(salaryHistoryRequest)) {
            return null;
        }
        return SalaryHistory.builder().
                id(salaryHistoryRequest.getId())
                .currency(salaryHistoryRequest.getCurrency())
                .startDate(salaryHistoryRequest.getStartDate())
                .endDate(salaryHistoryRequest.getEndDate())
                .salaryAmount(salaryHistoryRequest.getSalaryAmount())
                .hr(HumanResource.builder().id(salaryHistoryRequest.getHrId()).build())
                .build();
    }
}
