package com.talenteo.hr.service;

import com.talenteo.hr.HrMsApplication;
import com.talenteo.hr.model.entity.Address;
import com.talenteo.hr.repository.AddressRepository;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.Before;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.inject.Inject;
import java.util.Optional;


@Slf4j
@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@SpringBootTest(classes = HrMsApplication.class)
public class AddressServiceTest {

    @Inject
    private AddressRepository addressRepository;

    @Inject
    private ValidationService validationService;


    @Test
    public void should_return_200_when_returning_address() {
        Optional<Address> address = addressRepository.findById(1L);
        Assertions.assertThat(address).isNotEmpty();
        Assertions.assertThat(address.get().getCountry()).isEqualTo("Tunisie");
    }

    @Test
    public void should_return_404_address_not_found() {
        Optional<Address> address = addressRepository.findByCountry("Italie");
        Assertions.assertThat(address).isNotPresent();
    }


}
