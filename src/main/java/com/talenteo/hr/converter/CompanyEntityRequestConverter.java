package com.talenteo.hr.converter;

import com.talenteo.hr.dto.CompanyEntityRequest;
import com.talenteo.hr.model.entity.CompanyEntity;
import lombok.Data;
import org.springframework.core.convert.converter.Converter;

import java.util.Objects;

@Data(staticConstructor = "newInstance")
public class CompanyEntityRequestConverter implements Converter<CompanyEntityRequest, CompanyEntity> {

    @Override
    public CompanyEntity convert(CompanyEntityRequest CompanyEntityRequest) {
        if (Objects.isNull(CompanyEntityRequest)) {
            return null;
        }
        return CompanyEntity.builder()
                .label(CompanyEntityRequest.getLabel())
                .email(CompanyEntityRequest.getEmail())
                .phoneNumber(CompanyEntityRequest.getPhoneNumber())
                .company(CompanyDtoConverter.newInstance().convert(CompanyEntityRequest.getCompany()))
                .address(AddressDtoConverter.newInstance().convert(CompanyEntityRequest.getAddress()))
                .isActive(CompanyEntityRequest.getIsActive())
                .build();
    }


}
