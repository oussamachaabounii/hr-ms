package com.talenteo.hr.converter;

import com.talenteo.hr.dto.CompanyEntityDto;
import com.talenteo.hr.model.entity.CompanyEntity;
import lombok.Data;
import org.springframework.core.convert.converter.Converter;

import java.util.Objects;

@Data(staticConstructor = "newInstance")
public class CompanyEntityDtoConverter implements Converter<CompanyEntityDto, CompanyEntity> {

    @Override
    public CompanyEntity convert(CompanyEntityDto companyEntityDto) {
        if (Objects.isNull(companyEntityDto)) {
            return null;
        }
        return CompanyEntity.builder()
                .id(companyEntityDto.getId())
                .label(companyEntityDto.getLabel())
                .email(companyEntityDto.getEmail())
                .phoneNumber(companyEntityDto.getPhoneNumber())
                .company(CompanyDtoConverter.newInstance().convert(companyEntityDto.getCompany()))
                .address(AddressDtoConverter.newInstance().convert(companyEntityDto.getAddress()))
                .isActive(companyEntityDto.getIsActive())
                .build();
    }


}