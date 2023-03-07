package com.talenteo.hr.service;


import com.easyms.rest.ms.rest.Validator;
import com.talenteo.common.error.TalenteoCommonMessages;
import com.talenteo.hr.dto.*;
import com.talenteo.hr.model.entity.*;
import com.talenteo.hr.repository.*;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.client.ResourceAccessException;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Component
@AllArgsConstructor
@Transactional
@Slf4j
public class ValidationService {


    private final HumanResourceRepository humanResourceRepository;
    private final AddressRepository addressRepository;
    private final CompanyRepository companyRepository;
    private final CompanyEntityRepository companyEntityRepository;
    private final SalaryHistoryRepository salaryHistoryRepository;

    public void validateCreateHumanResource(HumanResourceRequest humanResourceRequest) {
        Optional<HumanResource> humanResource = humanResourceRepository.findByEmail(humanResourceRequest.getEmail());

        Validator.of(humanResource)
                .validateIf(Optional::isPresent, () -> new IllegalStateException(TalenteoCommonMessages.email_used.getErrorKey()))
                .execute();

    }


    public void validateUpdateHumanResource(Long id, HumanResourceRequest humanResourceRequest) {
        HumanResource humanResource = humanResourceRepository.findById(id).orElse(null);
        Validator.of(humanResource)
                .validateIf(Objects::isNull, () -> new ResourceAccessException(TalenteoCommonMessages.hr_not_found.getErrorKey()))
                .ifValid()
                .ifMatch(humanResource.getEmail(), email -> !email.equalsIgnoreCase(humanResourceRequest.getEmail()))
                .validateIf(hr -> this.emailAlreadyUsed(humanResourceRequest.getEmail(), id), () -> new IllegalStateException(TalenteoCommonMessages.email_used.getErrorKey()))
                .execute();
    }

    public void validateCreateCompanyEntity(CompanyEntityRequest companyEntityRequest) {
        CompanyEntity companyEntity = companyEntityRepository.findByLabel(companyEntityRequest.getLabel()).orElse(null);
        Validator.of(companyEntity)
                .validateIf(Objects::nonNull, () -> new IllegalStateException(TalenteoCommonMessages.label_used.getErrorKey()))
                .execute();
    }

    public void validateUpdateCompanyEntity(Long id, CompanyEntityRequest companyEntityRequest) {
        CompanyEntity companyEntity = companyEntityRepository.findByLabel(companyEntityRequest.getLabel()).orElse(null);
        Validator.of(companyEntity)
                .validateIf(Objects::isNull, () -> new ResourceAccessException(TalenteoCommonMessages.company_not_found.getErrorKey()))
                .ifValid()
                .ifMatch(companyEntity.getLabel(), label -> !label.equalsIgnoreCase(companyEntity.getLabel()))
                .validateIf(hr -> this.labelAlreadyUsed(companyEntity.getLabel(), id), () -> new IllegalStateException(TalenteoCommonMessages.label_used.getErrorKey()))
                .execute();
    }

    public void validateCreateCompany(CompanyRequest companyRequest) {
        Company company = companyRepository.findByLabel(companyRequest.getLabel()).orElse(null);
        Validator.of(company)
                .validateIf(Objects::nonNull, () -> new IllegalStateException(TalenteoCommonMessages.label_used.getErrorKey()))
                .execute();
    }

    public void validateUpdateCompany(Long id, CompanyRequest companyRequest) {
        Company company = companyRepository.findById(id).orElse(null);
        Validator.of(company)
                .validateIf(Objects::isNull, () -> new ResourceAccessException(TalenteoCommonMessages.company_not_found.getErrorKey()))
                .ifValid()
                .ifMatch(company.getLabel(), label -> !label.equalsIgnoreCase(companyRequest.getLabel()))
                .validateIf(hr -> this.labelAlreadyUsed(companyRequest.getLabel(), id), () -> new IllegalStateException(TalenteoCommonMessages.label_used.getErrorKey()))
                .execute();
    }

    public void validateUpdateAddress(Long id, AddressRequest addressRequest) {
        Address address = addressRepository.findById(id).orElse(null);
        Validator.of(address)
                .validateIf(Objects::isNull, () -> new ResourceAccessException(TalenteoCommonMessages.address_not_found.getErrorKey()))
                .ifValid()
                .execute();
    }


    private boolean emailAlreadyUsed(String email, Long id) {
        HumanResource humanResource = humanResourceRepository.findByEmail(email).orElse(null);
        return Objects.nonNull(humanResource) && !humanResource.getId().equals(id);
    }

    private boolean labelAlreadyUsed(String label, Long id) {
        Company company = companyRepository.findByLabel(label).orElse(null);
        return Objects.nonNull(company) && !company.getId().equals(id);
    }

    public void validateCreateSalaryHistory(SalaryHistoryRequest salaryHistoryRequest) {
        List<SalaryHistory> historyList = Optional.of(salaryHistoryRepository.findByHr_Id(salaryHistoryRequest.getHrId())).orElse(null);

        Validator.of(historyList)
                .validateIf(Objects::isNull, () -> new ResourceAccessException(TalenteoCommonMessages.hr_not_found.getErrorKey()))
                .ifValid()
                .execute();


    }
}
