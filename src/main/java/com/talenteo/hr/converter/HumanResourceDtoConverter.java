package com.talenteo.hr.converter;

import com.talenteo.hr.dto.HumanResourceDto;
import com.talenteo.hr.model.entity.HumanResource;
import lombok.Data;
import org.springframework.core.convert.converter.Converter;

import java.util.Objects;

@Data(staticConstructor = "newInstance")
public class HumanResourceDtoConverter implements Converter<HumanResourceDto, HumanResource> {
    @Override
    public HumanResource convert(HumanResourceDto humanResourceDto) {
        if (Objects.isNull(humanResourceDto)) {
            return null;
        }
        return HumanResource.builder()
                .id(humanResourceDto.getId())
                .firstname(humanResourceDto.getFirstname())
                .lastname(humanResourceDto.getLastname())
                .email(humanResourceDto.getEmail())
                .gender(humanResourceDto.getGender())
                .avatar(humanResourceDto.getAvatar())
                .role(humanResourceDto.getRole())
                .birthDate(humanResourceDto.getBirthDate())
                .phoneNumber(humanResourceDto.getPhoneNumber())
                .address(AddressDtoConverter.newInstance().convert(humanResourceDto.getAddress()))
                .visibility(humanResourceDto.getVisibility())
                //.supervisor(HumanResourceDtoConverter.newInstance().convert(humanResourceDto.getSupervisor()))
                .companyEntity(CompanyEntityDtoConverter.newInstance().convert(humanResourceDto.getCompanyEntity()))
                .alreadyLoggedIn(humanResourceDto.isAlreadyLoggedIn())
.isActive(humanResourceDto.isActive())
                .build();
    }
}