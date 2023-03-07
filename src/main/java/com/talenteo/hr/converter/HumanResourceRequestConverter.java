package com.talenteo.hr.converter;


import com.talenteo.hr.dto.HumanResourceRequest;
import com.talenteo.hr.model.entity.HumanResource;
import com.talenteo.hr.model.entity.Nationality;
import com.talenteo.hr.repository.NationalityRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@AllArgsConstructor
@Component
//@Data(staticConstructor = "newInstance")
public class HumanResourceRequestConverter implements Converter<HumanResourceRequest, HumanResource> {


    private final NationalityRepository nationalityRepository;

    @Override
    public HumanResource convert(HumanResourceRequest humanResourceRequest) {
        if (Objects.isNull(humanResourceRequest)) {
            return null;
        }
        List<Nationality> nationalityList = Objects.nonNull(humanResourceRequest.getNationalities()) ? humanResourceRequest.getNationalities().stream()
                .map(nationality -> nationalityRepository.findByCountry(nationality)).collect(Collectors.toList()) : Arrays.asList();

        return HumanResource.builder()
                .id(humanResourceRequest.getId())
                .firstname(humanResourceRequest.getFirstname())
                .lastname(humanResourceRequest.getLastname())
                .email(humanResourceRequest.getEmail())
                .gender(humanResourceRequest.getGender())
                .avatar(humanResourceRequest.getAvatar())
                .role(humanResourceRequest.getRole())
                .birthDate(humanResourceRequest.getBirthDate())
                .phoneNumber(humanResourceRequest.getPhoneNumber())
                .address(AddressDtoConverter.newInstance().convert(humanResourceRequest.getAddress()))
                .supervisor(HumanResourceDtoConverter.newInstance().convert(humanResourceRequest.getSupervisor()))
                .visibility(humanResourceRequest.getVisibility())
                .companyEntity(CompanyEntityDtoConverter.newInstance().convert(humanResourceRequest.getCompanyEntity()))
                .alreadyLoggedIn(humanResourceRequest.isAlreadyLoggedIn())
                .nationalities(nationalityList)
                .isActive(humanResourceRequest.isActive())
                .build();
    }
}

