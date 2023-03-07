package com.talenteo.hr.service;

import com.talenteo.hr.converter.AddressConverter;
import com.talenteo.hr.converter.AddressRequestConverter;
import com.talenteo.hr.dto.AddressDto;
import com.talenteo.hr.dto.AddressRequest;
import com.talenteo.hr.model.entity.Address;
import com.talenteo.hr.repository.AddressRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Slf4j
@Component
@AllArgsConstructor
public class AddressService {

    private final AddressRepository addressRepository;


    public AddressDto create(AddressRequest AddressRequest) {
        Address hr = AddressRequestConverter.newInstance().convert(AddressRequest);
        Address Address = addressRepository.save(hr);
        return AddressConverter.newInstance().convert(Address);
    }


    public AddressDto update(Long id, AddressRequest AddressRequest) {
        Address hr = addressRepository.findById(id).orElse(null);
        hr.setAddress1(AddressRequest.getAddress1());
        hr.setAddress2(AddressRequest.getAddress2());
        hr.setZipCode(AddressRequest.getZipCode());
        hr.setCity(AddressRequest.getCity());
        hr.setCountry(AddressRequest.getCountry());
        Address Address = addressRepository.save(hr);
        return AddressConverter.newInstance().convert(hr);

    }


    public void delete(Long id) {
        Optional<Address> address = addressRepository.findById(id);
        addressRepository.delete(address.get());
    }

}
