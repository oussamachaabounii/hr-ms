package com.talenteo.hr.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.talenteo.hr.HrMsApplication;
import com.talenteo.hr.converter.AddressConverter;
import com.talenteo.hr.dto.*;
import com.talenteo.hr.model.entity.Address;
import com.talenteo.hr.model.entity.Company;
import com.talenteo.hr.repository.AddressRepository;
import com.talenteo.hr.repository.CompanyRepository;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.inject.Inject;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Optional;


@Slf4j
@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@SpringBootTest(classes = HrMsApplication.class)
public class HumanResourceServiceTest {

    @Inject
    private HumanResourceService humanResourceService;

    @Inject
    private AddressRepository addressRepository;

    @Inject
    private CompanyRepository companyRepository;


    public HumanResourceRequest get_human_resource() throws ParseException {
        Address address = addressRepository.findById(1L).orElse(null);
        Company company = companyRepository.findById(1L).orElse(null);

        HumanResourceRequest hrRequest = new HumanResourceRequest();
        hrRequest.setFirstname("skander");
        hrRequest.setLastname("lengliz");
        hrRequest.setGender(Gender.Male);
        Date birth_date = new SimpleDateFormat("yyyy-MM-dd").parse("1995-06-17");
        hrRequest.setBirthDate(birth_date);
        hrRequest.setPhoneNumber("+21621737013");
        hrRequest.setAddress(AddressConverter.newInstance().convert(address));
        return hrRequest;
    }


    @Test
    public void should_return_hr_when_getting_hr_by_id_and_hr_found() {
        Optional<HumanResourceDto> humanResourceDto = humanResourceService.getHrById(2L);
        Assertions.assertThat(humanResourceDto).isNotEmpty();

    }

    @Test
    public void should_return_null_when_getting_hr_by_id_and_hr_not_found() {
        Optional<HumanResourceDto> humanResourceDto = humanResourceService.getHrById(9999L);
        Assertions.assertThat(humanResourceDto).isEmpty();
    }

    @Test
    public void should_update_hr_successfully() {

        Optional<HumanResourceDto> hrbeforeUpdate = humanResourceService.getHrById(2L);
        log.info("before update : " + hrbeforeUpdate.get());

        HumanResourceRequest hrRequest = HumanResourceRequest.builder()
                .gender(Gender.Male)
                .birthDate(new Date())
                .avatar("avataar.jpg")
                .alreadyLoggedIn(true)
                .companyEntity(CompanyEntityDto.builder().build())
                .firstname("John")
                .lastname("Smith")
                .email("john.smith@gmail.com")
                .nationalities(Arrays.asList("tn"))
                .phoneNumber("06102548668")
                .visibility(Visibility.Entity)
                .role(Role.Consultant)
                .isActive(true)
                .build();
        AddressDto addressDto = AddressDto.builder()
                .address1("test adress1")
                .address2("test adress1")
                .city("test city")
                .country("test country")
                .zipCode("test zipcode")
                .build();
        hrRequest.setAddress(addressDto);

        HumanResourceDto humanResourceDto = humanResourceService.update(4L, hrRequest);
        log.info("after update firstname : " + humanResourceDto.getFirstname());
        log.info("after update adress1 : " + humanResourceDto.getAddress().getAddress1());
        ObjectMapper mapper = new ObjectMapper();
        try {
            String json = mapper.writeValueAsString(hrRequest);
            System.out.println("ResultingJSONstring = " + json);
            //System.out.println(json);
        } catch (JsonProcessingException e) {
            e.printStackTrace();

        }
    }


}
