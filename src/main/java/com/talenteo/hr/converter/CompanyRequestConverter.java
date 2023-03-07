package com.talenteo.hr.converter;

import com.talenteo.hr.dto.CompanyRequest;
import com.talenteo.hr.model.entity.Company;
import lombok.Data;
import org.springframework.core.convert.converter.Converter;

import java.util.Objects;

@Data(staticConstructor = "newInstance")
public class CompanyRequestConverter implements Converter<CompanyRequest, Company> {

    @Override
    public Company convert(CompanyRequest companyRequest) {
        if (Objects.isNull(companyRequest)) {
            return null;
        }
        return Company.builder()
                .label(companyRequest.getLabel())
                .description(companyRequest.getDescription())
                .phoneNumber(companyRequest.getPhoneNumber())
                .logo(companyRequest.getLogo())
                .activityField(companyRequest.getActivityField())
                .creationDate(companyRequest.getCreationDate())
                .size(companyRequest.getSize())
                .type(companyRequest.getType())
.isActive(companyRequest.getIsActive())
                .build();
    }


}
