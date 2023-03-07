package com.talenteo.hr.api;

import com.talenteo.hr.dto.AddressDto;
import com.talenteo.hr.dto.AddressRequest;
import com.talenteo.hr.service.AddressService;
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

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Slf4j
@RestController
@RequestMapping("/api")
@Validated
@AllArgsConstructor
@Timed
public class AddressResource {

    private final AddressService AddressService;
    private final ValidationService validationService;


    @ApiOperation("create new address")
    @Timed
    @PreAuthorize("hasAnyAuthority('PERM_CREATE_CONSULTANT','PERM_CREATE_MANAGER','PERM_CREATE_RECRUITER','PERM_CREATE_CANDIDATE','PERM_CREATE_COMPANY_ADMIN')")
    @PostMapping(produces = APPLICATION_JSON_VALUE, path = "/v1/addresses")
    ResponseEntity<AddressDto> createAddress(@RequestBody AddressRequest AddressRequest) {
        log.info("create new address");
        AddressDto AddressDto = AddressService.create(AddressRequest);
        final URI location = ServletUriComponentsBuilder.fromCurrentServletMapping().path("/api/v1/addresses/{id}").build().expand(AddressDto.getId()).toUri();
        return ResponseEntity.created(location).body(AddressDto);
    }


    @ApiOperation("update address by id")
    @Timed
    @PreAuthorize("hasAnyAuthority('PERM_CREATE_CONSULTANT','PERM_CREATE_MANAGER','PERM_CREATE_RECRUITER','PERM_CREATE_CANDIDATE','PERM_CREATE_COMPANY_ADMIN')")
    @PutMapping(produces = APPLICATION_JSON_VALUE, path = "/v1/addresses/{id}")
    ResponseEntity<AddressDto> updateAddress(@PathVariable Long id, @RequestBody @Valid AddressRequest AddressRequest) {
        log.info(" update address by id {}", id);
        validationService.validateUpdateAddress(id, AddressRequest);
        AddressDto AddressDto = AddressService.update(id, AddressRequest);
        return ResponseEntity.ok().body(AddressDto);

    }

    @ApiOperation("delete address")
    @Timed
    @PreAuthorize("hasAnyAuthority('PERM_CREATE_CONSULTANT','PERM_CREATE_MANAGER','PERM_CREATE_RECRUITER','PERM_CREATE_CANDIDATE','PERM_CREATE_COMPANY_ADMIN')")
    @DeleteMapping(produces = APPLICATION_JSON_VALUE, path = "/v1/addresses/{id}")
    ResponseEntity<Void> deleteAddress(@PathVariable Long id) {
        log.info("delete address by id {}", id);
        AddressService.delete(id);
        return ResponseEntity.ok().build();
    }

}
