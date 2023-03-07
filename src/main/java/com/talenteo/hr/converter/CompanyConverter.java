package com.talenteo.hr.converter;


import com.talenteo.hr.dto.CompanyDto;
import com.talenteo.hr.model.entity.Company;
import lombok.Data;
import org.springframework.core.convert.converter.Converter;

import java.util.Objects;

@Data(staticConstructor = "newInstance")
public class CompanyConverter implements Converter<Company, CompanyDto> {

    @Override
    public CompanyDto convert(Company company) {
        if (Objects.isNull(company)) {
            return null;
        }
        return CompanyDto.builder()
                .id(company.getId())
                .label(company.getLabel())
                .logo(company.getLogo())
                .description(company.getDescription())
                .phoneNumber(company.getPhoneNumber())
                .activityField(company.getActivityField())
                .creationDate(company.getCreationDate())
                .size(company.getSize())
                .type(company.getType())
                .isActive(company.getIsActive())
                .build();
    }


}
