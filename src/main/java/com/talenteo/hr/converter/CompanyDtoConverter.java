package com.talenteo.hr.converter;


import com.talenteo.hr.dto.CompanyDto;
import com.talenteo.hr.model.entity.Company;
import lombok.Data;
import org.springframework.core.convert.converter.Converter;

import java.util.Objects;

@Data(staticConstructor = "newInstance")
public class CompanyDtoConverter implements Converter<CompanyDto, Company> {

    @Override
    public Company convert(CompanyDto companyDto) {
        if (Objects.isNull(companyDto)) {
            return null;
        }
        return Company.builder()
                .id(companyDto.getId())
                .label(companyDto.getLabel())
                .logo(companyDto.getLogo())
                .description(companyDto.getDescription())
                .phoneNumber(companyDto.getPhoneNumber())
                .activityField(companyDto.getActivityField())
                .creationDate(companyDto.getCreationDate())
                .size(companyDto.getSize())
                .type(companyDto.getType())
                .build();

    }
}