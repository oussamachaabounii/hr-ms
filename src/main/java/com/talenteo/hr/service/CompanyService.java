package com.talenteo.hr.service;

import com.talenteo.hr.converter.CompanyConverter;
import com.talenteo.hr.converter.CompanyRequestConverter;
import com.talenteo.hr.dto.CompanyDto;
import com.talenteo.hr.dto.CompanyRequest;
import com.talenteo.hr.model.entity.Company;
import com.talenteo.hr.repository.CompanyEntityRepository;
import com.talenteo.hr.repository.CompanyRepository;
import com.talenteo.hr.repository.HumanResourceRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@AllArgsConstructor
public class CompanyService {

    private final CompanyRepository companyRepository;
    private final CompanyEntityRepository companyEntityRepository;
    private final HumanResourceRepository humanResourceRepository;
    private final HumanResourceService humanResourceService;
    private final CompanyEntityService companyEntityService;



    public CompanyDto create(CompanyRequest companyRequest) {
        Company c = CompanyRequestConverter.newInstance().convert(companyRequest);
        c.setIsActive(true);
        Company company = companyRepository.save(c);
        return CompanyConverter.newInstance().convert(company);
    }

    public List<CompanyDto> getAll() {
        List<Company> companyDtos = companyRepository.findAllCompanies();
        return companyDtos.stream().map(company -> CompanyConverter.newInstance().convert(company)).collect(Collectors.toList());

  }

    public Optional<CompanyDto> getById(Long id) {
        Optional<Company> company = companyRepository.findById(id);
        return company.map(c -> CompanyConverter.newInstance().convert(c));
    }

    public Optional<CompanyDto> getByLabel(String label) {
        Optional<Company> company = companyRepository.findByLabel(label);
        return company.map(c -> CompanyConverter.newInstance().convert(c));
    }

    public void delete(Long id) {
        Optional<Company> c = companyRepository.findById(id);
      c.get().setIsActive(false);
       companyRepository.save(c.get());




    }

    public List<CompanyDto> filterCompaniesByKeyword(String query) {
        List<Company> c = companyRepository.findByKeyword(query);
        return c.stream().map(company -> CompanyConverter.newInstance().convert(company)).collect(Collectors.toList());

    }

    public CompanyDto update(Long id, CompanyRequest companyRequest) {
        Company c = companyRepository.findById(id).orElse(null);
        c.setActivityField(companyRequest.getActivityField());
        c.setCreationDate(companyRequest.getCreationDate());
        c.setDescription(companyRequest.getDescription());
        c.setLabel(companyRequest.getLabel());
        c.setPhoneNumber(companyRequest.getPhoneNumber());
        c.setSize(companyRequest.getSize());
        c.setType(companyRequest.getType());
        Company company = companyRepository.save(c);
        return CompanyConverter.newInstance().convert(company);

    }

    public List<List> companiesStatistics() {
        List<String> listLabels=new ArrayList<>();
        List<Integer> listNumberEntities=new ArrayList<>();
        List<Integer> listNumberAccounts=new ArrayList<>();
        List<List> listResult=new ArrayList<>();
        List<Company> companies = companyRepository.findAll();
        for(int i=0;i<companies.size();i++){
            if (companies.get(i).getIsActive()==true) {
                listLabels.add(companies.get(i).getLabel());
                listNumberEntities.add(companyEntityService.getByCompanyId(companies.get(i).getId()).size());
                listNumberAccounts.add(humanResourceService.getAllByCompanyId(companies.get(i).getId()).size());
            }
        }
        listResult.add(listLabels);
        listResult.add(listNumberEntities);
        listResult.add(listNumberAccounts);



        return listResult;

    }

}
