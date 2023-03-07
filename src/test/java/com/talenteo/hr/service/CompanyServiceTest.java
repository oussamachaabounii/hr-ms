package com.talenteo.hr.service;

import com.talenteo.hr.HrMsApplication;
import com.talenteo.hr.dto.CompanyRequest;
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
import java.util.Date;
import java.util.Optional;


@Slf4j
@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@SpringBootTest(classes = HrMsApplication.class)
public class CompanyServiceTest {

    @Inject
    private CompanyService companyService;

    @Inject
    private CompanyRepository companyRepository;

    @Inject
    private AddressRepository addressManager;


    @Test
    public void should_return_200_when_returning_company() throws ParseException {
        Optional<Company> company = companyRepository.findById(1L);
        Assertions.assertThat(company).isNotEmpty();
        Assertions.assertThat(company.get().getLabel()).isEqualTo("Nearteam");
    }

    @Test
    public void should_return_404_company_not_found() throws ParseException {
        Optional<Company> company = companyRepository.findByLabel("Captiva");
        Assertions.assertThat(company).isNotPresent();
    }

    public CompanyRequest get_company() throws ParseException {
        Optional<Address> address = addressManager.findById(1L);

        CompanyRequest companyRequest = new CompanyRequest();
        companyRequest.setActivityField("finance");
        Date creation_date = new SimpleDateFormat("yyyy-MM-dd").parse("1995-06-17");
        companyRequest.setCreationDate(creation_date);
        companyRequest.setDescription("company in finance");
        companyRequest.setLabel("Nearteam");
        companyRequest.setPhoneNumber("+21671456789");
        companyRequest.setSize("10-20");
        companyRequest.setType("Startup");

        return companyRequest;
    }


}
