package com.talenteo.hr.converter;


import com.talenteo.hr.dto.HumanResourceDto;
import com.talenteo.hr.model.entity.HumanResource;
import lombok.Data;
import org.checkerframework.checker.nullness.Opt;
import org.springframework.core.convert.converter.Converter;

import java.util.*;
import java.util.stream.Collectors;

@Data(staticConstructor = "newInstance")
public class HumanResourceConverter implements Converter<HumanResource, HumanResourceDto> {
    @Override
    public HumanResourceDto convert(HumanResource humanResource) {
        if (Objects.isNull(humanResource)) {
            return null;
        }
        List<String> nationalities = humanResource.getNationalities().stream().map(nationality -> nationality.getCountry()).collect(Collectors.toList());

        return HumanResourceDto.builder()
                .id(humanResource.getId())
                .firstname(humanResource.getFirstname())
                .lastname(humanResource.getLastname())
                .email(humanResource.getEmail())
                .gender(humanResource.getGender())
                .avatar(humanResource.getAvatar())
                .role(humanResource.getRole())
                .birthDate(humanResource.getBirthDate())
                .phoneNumber(humanResource.getPhoneNumber())
                .nationalities(nationalities)
                .address(AddressConverter.newInstance().convert(humanResource.getAddress()))
                .visibility(humanResource.getVisibility())
                .supervisorId(Optional.ofNullable(humanResource.getSupervisor()).map(HumanResource::getId).orElse(null))
                .companyEntity(CompanyEntityConverter.newInstance().convert(humanResource.getCompanyEntity()))
                .alreadyLoggedIn(humanResource.isAlreadyLoggedIn())
                //.salaryHistory(humanResource.getSalaryHistory().stream().map(salHis ->
                //       SalaryHistoryConverter.newInstance().convert(salHis)).collect(Collectors.toList()))
                .salaryHistory(Optional.ofNullable(humanResource.getSalaryHistory()).map(salaryHistories -> salaryHistories.stream().map(salHis ->
                    SalaryHistoryConverter.newInstance().convert(salHis)).collect(Collectors.toList())).orElse(Collections.EMPTY_LIST))
.isActive(humanResource.isActive())
                .build();
    }
}
