package com.talenteo.hr.converter;

import com.talenteo.hr.dto.HumanResourceLightDto;
import com.talenteo.hr.model.entity.HumanResource;
import lombok.Data;
import org.springframework.core.convert.converter.Converter;

import java.util.Objects;

@Data(staticConstructor = "newInstance")
public class HumanResourceLightConverter implements Converter<HumanResource, HumanResourceLightDto> {

    @Override
    public HumanResourceLightDto convert(HumanResource humanResource) {
        if (Objects.isNull(humanResource)) {
            return null;
        }

        return HumanResourceLightDto.builder()
                .id(humanResource.getId())
                .firstname(humanResource.getFirstname())
                .lastname(humanResource.getLastname())
                .email(humanResource.getEmail())
                .gender(humanResource.getGender())
                .avatar(humanResource.getAvatar())
                .role(humanResource.getRole())
                .birthDate(humanResource.getBirthDate())
                .phoneNumber(humanResource.getPhoneNumber())
                .visibility(humanResource.getVisibility())
.isActive(humanResource.isActive())
                .alreadyLoggedIn(humanResource.isAlreadyLoggedIn())
                .build();
    }
}
