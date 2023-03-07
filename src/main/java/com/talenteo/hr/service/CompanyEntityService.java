package com.talenteo.hr.service;

import com.talenteo.common.security.SecurityUtils;
import com.talenteo.hr.converter.CompanyEntityConverter;
import com.talenteo.hr.converter.CompanyEntityRequestConverter;
import com.talenteo.hr.dto.CompanyEntityDto;
import com.talenteo.hr.dto.CompanyEntityRequest;
import com.talenteo.hr.model.entity.Address;
import com.talenteo.hr.model.entity.Company;
import com.talenteo.hr.model.entity.CompanyEntity;
import com.talenteo.hr.model.entity.HumanResource;
import com.talenteo.hr.repository.AddressRepository;
import com.talenteo.hr.repository.CompanyEntityRepository;
import com.talenteo.hr.repository.CompanyRepository;
import com.talenteo.hr.repository.HumanResourceRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j


@Component
@AllArgsConstructor
public class CompanyEntityService {

    private final CompanyEntityRepository CompanyEntityRepository;
    private final CompanyRepository companyRepository;
    private final AddressRepository addressRepository;

    private final OAuthRemote oAuthRemote;
    private final HumanResourceRepository humanResourceRepository;

    public List<CompanyEntityDto> getAll() {
        List<CompanyEntity> CompanyEntityDtos = CompanyEntityRepository.findAllCompanyEntities();
        return CompanyEntityDtos.stream().map(CompanyEntity -> CompanyEntityConverter.newInstance().convert(CompanyEntity)).collect(Collectors.toList());
    }


    public Optional<CompanyEntityDto> getById(Long id) {
        Optional<CompanyEntity> companyEntity = CompanyEntityRepository.findById(id);
        return companyEntity.map(c -> CompanyEntityConverter.newInstance().convert(c));
    }

    public List<CompanyEntityDto> getByCompanyId(Long id) {
        List<CompanyEntity> companyEntity = CompanyEntityRepository.findByCompanyId(id);
        return companyEntity.stream().map(c -> CompanyEntityConverter.newInstance().convert(c)).collect(Collectors.toList());
    }


    public CompanyEntityDto create(CompanyEntityRequest CompanyEntityRequest) {
        Optional<HumanResource> currentHr = humanResourceRepository.findByEmail(SecurityUtils.getCurrentUserId());
        Optional<Company> company = companyRepository.findById(CompanyEntityRequest.getCompany().getId());
        Optional<Address> address = addressRepository.findById(CompanyEntityRequest.getAddress().getId());

        CompanyEntity c = CompanyEntityRequestConverter.newInstance().convert(CompanyEntityRequest);
        c.setCompany(company.get());
        c.setAddress(address.get());
        c.setLabel(CompanyEntityRequest.getLabel());
        c.setIsActive(true);
        CompanyEntity CompanyEntity = CompanyEntityRepository.save(c);
        return CompanyEntityConverter.newInstance().convert(CompanyEntity);
    }


    public CompanyEntityDto update(Long id, CompanyEntityRequest CompanyEntityRequest) {
        CompanyEntity c = CompanyEntityRepository.findById(id).orElse(null);
        c.setLabel(CompanyEntityRequest.getLabel());
        c.setEmail(CompanyEntityRequest.getEmail());
        c.setPhoneNumber(CompanyEntityRequest.getPhoneNumber());
        CompanyEntityRepository.save(c);
        return CompanyEntityConverter.newInstance().convert(c);
    }


    public void delete(Long id) {
        Optional<CompanyEntity> CompanyEntity = CompanyEntityRepository.findById(id);
        CompanyEntity.get().setIsActive(false);
        CompanyEntityRepository.save(CompanyEntity.get());
    }


}
