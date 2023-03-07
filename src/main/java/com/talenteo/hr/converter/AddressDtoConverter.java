package com.talenteo.hr.converter;

import com.talenteo.hr.dto.AddressDto;
import com.talenteo.hr.model.entity.Address;
import lombok.Data;
import org.springframework.core.convert.converter.Converter;

import java.util.Objects;

@Data(staticConstructor = "newInstance")
public class AddressDtoConverter implements Converter<AddressDto, Address> {

    @Override
    public Address convert(AddressDto addressDto) {
        if (Objects.isNull(addressDto)) {
            return null;
        }
        return Address.builder()
                .id(addressDto.getId())
                .address1(addressDto.getAddress1())
                .address2(addressDto.getAddress2())
                .city(addressDto.getCity())
                .country(addressDto.getCountry())
                .zipCode(addressDto.getZipCode())
                .build();
    }
}