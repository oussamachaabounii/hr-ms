package com.talenteo.hr.service;

import com.talenteo.common.error.TalenteoCommonMessages;
import com.talenteo.hr.HrMsApplication;
import com.talenteo.hr.dto.*;
import com.talenteo.hr.model.entity.Address;
import com.talenteo.hr.model.entity.Company;
import com.talenteo.hr.model.entity.CompanyEntity;
import com.talenteo.hr.model.entity.HumanResource;
import com.talenteo.hr.repository.AddressRepository;
import com.talenteo.hr.repository.CompanyEntityRepository;
import com.talenteo.hr.repository.CompanyRepository;
import com.talenteo.hr.repository.HumanResourceRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.inject.Inject;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;

@Slf4j
@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@SpringBootTest(classes = HrMsApplication.class)
public class ValidationServiceTest {

    @Inject
    private ValidationService validationService;

    @MockBean
    private HumanResourceRepository humanResourceRepository;

    @MockBean
    private AddressRepository addressRepository;

    @MockBean
    private CompanyEntityRepository companyEntityRepository;

    @MockBean
    private CompanyRepository companyRepository;


    private HumanResourceRequest getHumanResourceRequest() {

        return HumanResourceRequest.builder()
                .firstname("john")
                .lastname("doe")
                .email("johndoe@gmail.com")
                .phoneNumber("+336584257462014")
                .address(AddressDto.builder().id(1L).build())
                .companyEntity(CompanyEntityDto.builder().id(1L).build())
                .supervisor(HumanResourceDto.builder().id(1L).build())
                .build();
    }

    private CompanyRequest getCompanyRequest() {

        return CompanyRequest.builder()
                .label("Captiva")
                .build();
    }

    private CompanyEntityRequest getCompanyEntityRequest() {
        return CompanyEntityRequest.builder()
                .label("Captiva Tunisie")
                .build();
    }

    private AddressRequest getAddressRequest() {
        return AddressRequest.builder()
                .address1("Centre Urbain Nord")
                .zipCode("1080")
                .city("Tunis")
                .country("Tunisie")
                .build();
    }

    /**************************************   validateCreateHumanResource   *****************************************/

    @Test
    public void should_return_ok_when_creating_human_resource_with_valid_request() {
        Mockito.doReturn(Optional.empty()).when(humanResourceRepository).findByEmail(anyString());
        Mockito.doReturn(Optional.of(new Address())).when(addressRepository).findById(anyLong());
        Mockito.doReturn(Optional.of(new Company())).when(companyRepository).findById(anyLong());

        Throwable throwable = catchThrowable(() -> validationService.validateCreateHumanResource(getHumanResourceRequest()));
        assertThat(throwable).isNull();
    }

    @Test
    public void should_return_email_used_exception_when_creating_human_resource_with_existing_email() {
        Mockito.doReturn(Optional.of(new HumanResource())).when(humanResourceRepository).findByEmail(anyString());
        Mockito.doReturn(Optional.empty()).when(addressRepository).findById(anyLong());
        Mockito.doReturn(Optional.empty()).when(companyRepository).findById(anyLong());

        Throwable throwable = catchThrowable(() -> validationService.validateCreateHumanResource(getHumanResourceRequest()));
        assertThat(throwable).isNotNull();
        List<String> messages = Arrays.stream(throwable.getSuppressed()).map(Throwable::getMessage).collect(Collectors.toList());
        assertThat(messages).hasSize(1);
        assertThat(messages).containsExactlyInAnyOrder(TalenteoCommonMessages.email_used.getErrorKey());
    }

    /**************************************   validateUpdateHumanResource   *****************************************/

    @Test
    public void should_return_ok_when_updating_human_resource_with_valid_request_and_id() {
        HumanResource hr = new HumanResource();
        hr.setEmail("johndoe@yopmail.com");
        Mockito.doReturn(Optional.of(hr)).when(humanResourceRepository).findById(anyLong());

        Throwable throwable = catchThrowable(() -> validationService.validateUpdateHumanResource(1L, getHumanResourceRequest()));
        assertThat(throwable).isNull();
    }

    @Test
    public void should_return_not_found_when_updating_human_resource_with_invalid_request() {
        Mockito.doReturn(Optional.empty()).when(humanResourceRepository).findById(anyLong());
        Mockito.doReturn(Optional.of(HumanResource.builder().id(45L).build())).when(humanResourceRepository).findByEmail(anyString());

        Throwable throwable = catchThrowable(() -> validationService.validateUpdateHumanResource(2L, getHumanResourceRequest()));
        assertThat(throwable).isNotNull();
        assertThat(throwable.getMessage()).isEqualTo(TalenteoCommonMessages.hr_not_found.getErrorKey());
    }

    @Test
    public void should_return_bad_request_when_updating_human_resource_with_existing_email() {
        Mockito.doReturn(Optional.of(HumanResource.builder().email("johndoe@yopmail.com").build())).when(humanResourceRepository).findById(anyLong());
        Mockito.doReturn(Optional.of(HumanResource.builder().id(45L).build())).when(humanResourceRepository).findByEmail(anyString());

        Throwable throwable = catchThrowable(() -> validationService.validateUpdateHumanResource(2L, getHumanResourceRequest()));
        assertThat(throwable).isNotNull();
        assertThat(throwable.getMessage()).isEqualTo(TalenteoCommonMessages.email_used.getErrorKey());
    }


    @Test
    public void should_return_not_found_when_updating_human_resource_with_non_existing_hr_id() {
        Mockito.doReturn(Optional.empty()).when(humanResourceRepository).findById(anyLong());

        Throwable throwable = catchThrowable(() -> validationService.validateUpdateHumanResource(1L, getHumanResourceRequest()));
        assertThat(throwable).isNotNull();
        assertThat(throwable.getMessage()).isEqualTo(TalenteoCommonMessages.hr_not_found.getErrorKey());
    }

    /**************************************   validateCreateCompany   *****************************************/

    @Test
    public void should_return_ok_when_creating_company_with_valid_request() {
        Mockito.doReturn(Optional.of(new Address())).when(addressRepository).findById(anyLong());
        Mockito.doReturn(Optional.empty()).when(companyRepository).findByLabel(anyString());

        Throwable throwable = catchThrowable(() -> validationService.validateCreateCompany(getCompanyRequest()));
        assertThat(throwable).isNull();
    }

    @Test
    public void should_return_multiple_exceptions_when_creating_company_with_existing_label() {
        Mockito.doReturn(Optional.empty()).when(addressRepository).findById(anyLong());
        Mockito.doReturn(Optional.of(new Company())).when(companyRepository).findByLabel(anyString());

        Throwable throwable = catchThrowable(() -> validationService.validateCreateCompany(getCompanyRequest()));
        assertThat(throwable).isNotNull();
        List<String> messages = Arrays.stream(throwable.getSuppressed()).map(Throwable::getMessage).collect(Collectors.toList());
        assertThat(messages).hasSize(1);
        assertThat(messages).containsExactlyInAnyOrder("label.used");
    }

    @Test
    public void should_return_bad_request_when_creating_company_with_existing_label() {
        Mockito.doReturn(Optional.of(new Address())).when(addressRepository).findById(anyLong());
        Mockito.doReturn(Optional.of(new Company())).when(companyRepository).findByLabel(anyString());

        Throwable throwable = catchThrowable(() -> validationService.validateCreateCompany(getCompanyRequest()));
        assertThat(throwable).isNotNull();
        assertThat(throwable.getMessage()).isEqualTo(TalenteoCommonMessages.label_used.getErrorKey());
    }



    /**************************************   validateUpdateCompany   *****************************************/

    @Test
    public void should_return_ok_when_updating_company_with_valid_request_and_id() {
        Mockito.doReturn(Optional.of(new Address())).when(addressRepository).findById(anyLong());
        Mockito.doReturn(Optional.of(Company.builder().label("Nearteam").build())).when(companyRepository).findById(anyLong());

        Throwable throwable = catchThrowable(() -> validationService.validateUpdateCompany(1L, getCompanyRequest()));
        assertThat(throwable).isNull();
    }

    @Test
    public void should_return_not_found_when_updating_company_with_existing_label_and_non_existing_company_id() {
        Mockito.doReturn(Optional.empty()).when(companyRepository).findById(anyLong());
        Mockito.doReturn(Optional.of(Company.builder().id(40L).build())).when(companyRepository).findByLabel(anyString());

        Throwable throwable = catchThrowable(() -> validationService.validateUpdateCompany(1L, getCompanyRequest()));
        assertThat(throwable).isNotNull();
        assertThat(throwable.getMessage()).isEqualTo(TalenteoCommonMessages.company_not_found.getErrorKey());

    }

    @Test
    public void should_return_not_found_when_updating_company_with_non_existing_company_id() {
        Mockito.doReturn(Optional.empty()).when(companyRepository).findById(anyLong());

        Throwable throwable = catchThrowable(() -> validationService.validateUpdateCompany(1L, getCompanyRequest()));
        assertThat(throwable).isNotNull();
        assertThat(throwable.getMessage()).isEqualTo(TalenteoCommonMessages.company_not_found.getErrorKey());

    }

    @Test
    public void should_return_bad_request_when_updating_company_with_existing_label() {
        Mockito.doReturn(Optional.of(Company.builder().label("Nearteam").build())).when(companyRepository).findById(anyLong());
        Mockito.doReturn(Optional.of(Company.builder().id(40L).build())).when(companyRepository).findByLabel(anyString());

        Throwable throwable = catchThrowable(() -> validationService.validateUpdateCompany(1L, getCompanyRequest()));
        assertThat(throwable).isNotNull();
        assertThat(throwable.getMessage()).isEqualTo(TalenteoCommonMessages.label_used.getErrorKey());
    }

    /**************************************   validateUpdateAddress   *****************************************/

    @Test
    public void should_return_ok_when_updating_address_with_valid_request() {
        Mockito.doReturn(Optional.of(Address.builder().address1("Centre Urbain Nord").build())).when(addressRepository).findById(anyLong());

        Throwable throwable = catchThrowable(() -> validationService.validateUpdateAddress(1L, getAddressRequest()));
        assertThat(throwable).isNull();
    }

    @Test
    public void should_return_not_found_when_updating_address_with_non_existing_address_id() {
        Mockito.doReturn(Optional.empty()).when(addressRepository).findById(anyLong());

        Throwable throwable = catchThrowable(() -> validationService.validateUpdateAddress(100L, getAddressRequest()));
        assertThat(throwable).isNotNull();
        assertThat(throwable.getMessage()).isEqualTo(TalenteoCommonMessages.address_not_found.getErrorKey());

    }

}
