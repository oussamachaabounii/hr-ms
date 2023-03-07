package com.talenteo.hr.converter;

import com.talenteo.hr.dto.AddressRequest;
import com.talenteo.hr.model.entity.Address;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.convert.converter.Converter;

import java.util.Objects;


@Slf4j

@Data(staticConstructor = "newInstance")

public class AddressRequestConverter implements Converter<AddressRequest, Address> {
    @Override
    public Address convert(AddressRequest address) {
        if (Objects.isNull(address)) {
            return null;
        }
        return Address.builder()
                .address1(address.getAddress1())
                .address2(address.getAddress2())
                .city(address.getCity())
                .country(address.getCountry())
                .zipCode(address.getZipCode())
                .build();
    }


}
