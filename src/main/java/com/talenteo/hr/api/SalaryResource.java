package com.talenteo.hr.api;

import com.talenteo.hr.dto.SalaryHistoryDto;
import com.talenteo.hr.dto.SalaryHistoryRequest;
import com.talenteo.hr.service.SalaryHistoryService;
import com.talenteo.hr.service.ValidationService;
import io.micrometer.core.annotation.Timed;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.Valid;
import java.net.URI;
import java.util.List;
import java.util.Optional;

import static org.springframework.util.MimeTypeUtils.APPLICATION_JSON_VALUE;

@Slf4j
@RestController
@RequestMapping("/api")
@Validated
@AllArgsConstructor
@Timed
public class SalaryResource {

    private final SalaryHistoryService salaryHistoryService;
    private final ValidationService validationService;


    /**
     * ======================================================
     * Get salary history for a hr
     * ======================================================
     *
     * @param resourceId resource id
     * @return ResponseEntity<List < SalaryHistoryDto>
     */
    @ApiOperation("get salary history for a hr")
    @Timed
    // @PreAuthorize("hasAnyAuthority('ROLE_SUPER_ADMIN')")
    @GetMapping(produces = APPLICATION_JSON_VALUE, path = "/v1/salaries")
    public ResponseEntity<List<SalaryHistoryDto>> getSalariesByResource(@RequestParam Long resourceId) {
        log.info("find wages by resource id  {} ", resourceId);
        List<SalaryHistoryDto> salariesByResourceId = salaryHistoryService.getAllByHrId(resourceId);
        return ResponseEntity.ok().body(salariesByResourceId);
    }

    /**
     * ======================================================
     * Get latest salary for a hr
     * ======================================================
     *
     * @param resourceId resource id
     * @return ResponseEntity<SalaryHistoryDto>
     */
    @ApiOperation("get latest salary for a hr")
    @Timed
    // @PreAuthorize("hasAnyAuthority('ROLE_SUPER_ADMIN')")
    @GetMapping(produces = APPLICATION_JSON_VALUE, path = "/v1/salaries/latest")
    public ResponseEntity<SalaryHistoryDto> getLatestSalaryByResource(@RequestParam Long resourceId) {
        log.info("find latest wage by resource id  {} ", resourceId);
        Optional<SalaryHistoryDto> salariesByResourceId = salaryHistoryService.getlatestByHrId(resourceId);
        return salariesByResourceId.map(salhisto -> ResponseEntity.ok().body(salhisto)).orElseGet(() -> ResponseEntity.notFound().build());
    }

    /**
     * ======================================================
     * Create new salary history
     * ======================================================
     *
     * @param salaryHistoryRequest
     * @return
     */
    @ApiOperation("Create new Salary history for a hr")
    @Timed
    // @PreAuthorize("hasAnyAuthority('ROLE_SUPER_ADMIN')")
    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE, path = "/v1/salaries")
    ResponseEntity<SalaryHistoryDto> createSalaryHistory(@RequestBody @Valid SalaryHistoryRequest salaryHistoryRequest) {
        log.info("create new Salary history for a hr");
        validationService.validateCreateSalaryHistory(salaryHistoryRequest);
        SalaryHistoryDto salaryHistoryDto = salaryHistoryService.create(salaryHistoryRequest);
        final URI location = ServletUriComponentsBuilder.fromCurrentServletMapping().path("/api/v1/salaries/{id}").build().expand(salaryHistoryDto.getId()).toUri();
        return ResponseEntity.created(location).body(salaryHistoryDto);
    }

}
