package com.talenteo.hr.api;

import com.talenteo.hr.dto.CompanyEntityDto;
import com.talenteo.hr.dto.CompanyEntityRequest;
import com.talenteo.hr.service.CompanyEntityService;
import com.talenteo.hr.service.ValidationService;
import io.micrometer.core.annotation.Timed;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
public class CompanyEntityResource {

    private CompanyEntityService CompanyEntityService;
    private final ValidationService validationService;


    @ApiOperation("search all company entities")
    @Timed
    @PreAuthorize("hasAnyAuthority('PERM_GET_COMPANY_ENTITY')")
    @GetMapping(produces = APPLICATION_JSON_VALUE, path = "/v1/company-entities")
    ResponseEntity<List<CompanyEntityDto>> getAllCompanyEntity() {
        log.info("get all company entities");
        List<CompanyEntityDto> CompanyEntityDtos = CompanyEntityService.getAll();
        return ResponseEntity.ok().body(CompanyEntityDtos);
    }

    @ApiOperation("search company entities by id")
    @Timed
    @PreAuthorize("hasAnyAuthority('PERM_GET_COMPANY_ENTITY')")
    @GetMapping(produces = APPLICATION_JSON_VALUE, path = "/v1/company-entities/{id}")
    ResponseEntity<CompanyEntityDto> getById(@PathVariable Long id) {
        log.info("get company entities by id {}", id);
        Optional<CompanyEntityDto> CompanyEntityDto = CompanyEntityService.getById(id);
        return CompanyEntityDto.map(companyEntity -> ResponseEntity.ok().body(companyEntity)).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @ApiOperation("search company entities by  company")
    @Timed
    @PreAuthorize("hasAnyAuthority('PERM_GET_COMPANY_ENTITY')")
    @GetMapping(produces = APPLICATION_JSON_VALUE, path = "/v1/company-entities/by-company")
    ResponseEntity<List<CompanyEntityDto>> getByCompany(@RequestParam(name = "id") Long id) {
        log.info("get company entities by company {}", id);
        List<CompanyEntityDto> CompanyEntityDto = CompanyEntityService.getByCompanyId(id);
        return ResponseEntity.ok().body(CompanyEntityDto);
    }


    @ApiOperation("create new CompanyEntity")
    @Timed
    @PreAuthorize("hasAnyAuthority('PERM_CREATE_COMPANY_ENTITY')")
    @PostMapping(produces = APPLICATION_JSON_VALUE, path = "/v1/company-entities")
    ResponseEntity<CompanyEntityDto> createCompanyEntity(@RequestBody CompanyEntityRequest CompanyEntityRequest) {
        log.info("create new company entity");
        CompanyEntityDto CompanyEntityDto = CompanyEntityService.create(CompanyEntityRequest);
        final URI location = ServletUriComponentsBuilder.fromCurrentServletMapping().path("/api/v1/company-entities/{id}").build().expand(CompanyEntityDto.getId()).toUri();

        return ResponseEntity.created(location).body(CompanyEntityDto);
    }


    @ApiOperation("update company entity by id")
    @Timed
    @PreAuthorize("hasAnyAuthority('PERM_UPDATE_COMPANY_ENTITY')")
    @PutMapping(produces = APPLICATION_JSON_VALUE, path = "/v1/company-entities/{id}")
    ResponseEntity<CompanyEntityDto> updateCompanyEntity(@PathVariable Long id, @RequestBody @Valid CompanyEntityRequest CompanyEntityRequest) {
        log.info("update company entity by id {}", id);
        CompanyEntityDto CompanyEntityDto = CompanyEntityService.update(id, CompanyEntityRequest);
        return ResponseEntity.ok().body(CompanyEntityDto);

    }

    @ApiOperation("delete company entity")
    @Timed
    @PreAuthorize("hasAnyAuthority('PERM_CREATE_COMPANY_ENTITY')")
    @DeleteMapping(produces = APPLICATION_JSON_VALUE, path = "/v1/company-entities/{id}")
    ResponseEntity<Void> deleteCompanyEntity(@PathVariable Long id) {
        log.info("delete CompanyEntity by id {}", id);
        CompanyEntityService.delete(id);
        return ResponseEntity.ok().build();
    }

}
