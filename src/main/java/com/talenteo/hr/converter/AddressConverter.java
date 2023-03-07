package com.talenteo.hr.converter;

import com.talenteo.hr.dto.AddressDto;
import com.talenteo.hr.model.entity.Address;
import lombok.Data;
import org.springframework.core.convert.converter.Converter;

import java.util.Objects;


@Data(staticConstructor = "newInstance")
public class AddressConverter implements Converter<Address, AddressDto> {
    @Override
    public AddressDto convert(Address address) {
        if (Objects.isNull(address)) {
            return null;
        }
        return AddressDto.builder()
                .id(address.getId())
                .address1(address.getAddress1())
                .address2(address.getAddress2())
                .city(address.getCity())
                .country(address.getCountry())
                .zipCode(address.getZipCode())
                .build();
    }
}
