package com.talenteo.hr.api;

import com.talenteo.hr.dto.CompanyDto;
import com.talenteo.hr.dto.CompanyRequest;
import com.talenteo.hr.service.CompanyService;
import com.talenteo.hr.service.ValidationService;
import io.micrometer.core.annotation.Timed;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.Valid;
import java.net.URI;
import java.util.List;
import java.util.Optional;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Slf4j
@RestController
@RequestMapping("/api")
@Validated
@AllArgsConstructor
@Timed
public class CompanyResource {

    private final CompanyService companyService;
    private final ValidationService validationService;


    @ApiOperation("create new company")
    @Timed
    @PreAuthorize("hasAnyAuthority('PERM_CREATE_COMPANY')")
    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE, path = "/v1/companies")
    ResponseEntity<CompanyDto> createCompany(@RequestBody @Valid CompanyRequest companyRequest) {
        log.info("create new company");
        validationService.validateCreateCompany(companyRequest);
        CompanyDto companyDto = companyService.create(companyRequest);
        final URI location = ServletUriComponentsBuilder.fromCurrentServletMapping().path("/api/v1/companies/{id}").build().expand(companyDto.getId()).toUri();
        return ResponseEntity.created(location).body(companyDto);
    }

    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", dataType = "int", paramType = "query",
                    value = "Results page you want to retrieve (0..N)", required = true),
            @ApiImplicitParam(name = "size", dataType = "int", paramType = "query",
                    value = "Number of records per page.", required = true),
            @ApiImplicitParam(name = "sort", allowMultiple = true, dataType = "string", paramType = "query",
                    value = "Sorting criteria in the format: property(,asc|desc). " +
                            "Default sort order is ascending. " +
                            "Multiple sort criteria are supported.")
    })
    @ApiOperation("search all companies")
    @Timed
   @PreAuthorize("hasAnyAuthority('PERM_GET_COMPANY')")
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE, path = "/v1/companies")
    ResponseEntity<List<CompanyDto>> getAllCompanies() {
        log.info("get all companies");
        List<CompanyDto> companyDtos = companyService.getAll();
        return ResponseEntity.ok().body(companyDtos);
    }

    @ApiOperation("search company by id")
    @Timed
    @PreAuthorize("hasAnyAuthority('PERM_GET_COMPANY')")
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE, path = "/v1/companies/{id}")
    ResponseEntity<CompanyDto> getCompanyById(@PathVariable Long id) {
        log.info("search Company by id {} ", id);
        Optional<CompanyDto> companyDto = companyService.getById(id);
        return companyDto.map(company -> ResponseEntity.ok().body(company)).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @ApiOperation("filter company by keyword")
    @Timed
    @PreAuthorize("hasAnyAuthority('PERM_GET_CANDIDATE','PERM_GET_CONSULTANT')")
    @GetMapping(produces = APPLICATION_JSON_VALUE, path = "/v1/companies/search-by-keyword")
    ResponseEntity<List<CompanyDto>> filterCompanyByKeyword(@RequestParam String query) {
        log.info("filter Human Resource by keyword = {}", query);
        List<CompanyDto> companyDto = companyService.filterCompaniesByKeyword(query);
        return ResponseEntity.ok().body(companyDto);
    }

    @ApiOperation("search company by label")
    @Timed
    @PreAuthorize("hasAnyAuthority('PERM_GET_COMPANY')")
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE, path = "/v1/companies/by-label")
    ResponseEntity<CompanyDto> getCompanyByLabel(@RequestParam String label) {
        log.info("search company by label {} ", label);
        Optional<CompanyDto> companyDto = companyService.getByLabel(label);
        return companyDto.map(company -> ResponseEntity.ok().body(company)).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @ApiOperation("update company by id")
    @Timed
    @PreAuthorize("hasAnyAuthority('PERM_UPDATE_COMPANY')")
    @PutMapping(produces = MediaType.APPLICATION_JSON_VALUE, path = "/v1/companies/{id}")
    ResponseEntity<CompanyDto> updateCompany(@PathVariable Long id, @RequestBody @Valid CompanyRequest companyRequest) {
        log.info("update company by id {}", id);
        validationService.validateUpdateCompany(id, companyRequest);
        CompanyDto companyDto = companyService.update(id, companyRequest);

        return ResponseEntity.ok().body(companyDto);

    }

    @ApiOperation("delete company by id")
    @Timed
    @PreAuthorize("hasAnyAuthority('PERM_CREATE_COMPANY')")
    @DeleteMapping(produces = MediaType.APPLICATION_JSON_VALUE, path = "/v1/companies/{id}")
    ResponseEntity<Void> deleteCompanyById(@PathVariable Long id) {
        log.info("delete Company by id {} ", id);
        companyService.delete(id);
        return ResponseEntity.ok().build();
    }

    @ApiOperation("get companies statistics")
    @Timed
    @PreAuthorize("hasAnyAuthority('PERM_GET_COMPANY')")
    @GetMapping(produces = APPLICATION_JSON_VALUE, path = "/v1/companies/statistics")
    ResponseEntity<List<List>> getCompaniesStatistics() {
        List<List> stats = companyService.companiesStatistics();
        return ResponseEntity.ok().body(stats);
    }

}
