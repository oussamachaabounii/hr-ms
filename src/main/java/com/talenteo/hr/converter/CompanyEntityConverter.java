package com.talenteo.hr.converter;


import com.talenteo.hr.dto.CompanyEntityDto;
import com.talenteo.hr.model.entity.CompanyEntity;
import lombok.Data;
import org.springframework.core.convert.converter.Converter;

import java.util.Objects;

@Data(staticConstructor = "newInstance")
public class CompanyEntityConverter implements Converter<CompanyEntity, CompanyEntityDto> {

    @Override
    public CompanyEntityDto convert(CompanyEntity CompanyEntity) {
        if (Objects.isNull(CompanyEntity)) {
            return null;
        }
        return CompanyEntityDto.builder()
                .id(CompanyEntity.getId())
                .label(CompanyEntity.getLabel())
                .email(CompanyEntity.getEmail())
                .phoneNumber(CompanyEntity.getPhoneNumber())
                .company(CompanyConverter.newInstance().convert(CompanyEntity.getCompany()))
                .address(AddressConverter.newInstance().convert(CompanyEntity.getAddress()))
                .isActive(CompanyEntity.getIsActive())
                .build();
    }


}
