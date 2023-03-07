package com.talenteo.hr.api;

import com.talenteo.hr.model.entity.VerificationToken;
import com.talenteo.hr.service.VerificationTokenService;
import io.micrometer.core.annotation.Timed;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Slf4j
@RestController
@RequestMapping("/api")
@Validated
@AllArgsConstructor
@Timed
public class VerificationTokenResource {
    private final VerificationTokenService verificationTokenService;

    @ApiOperation("Search verification token by token")
    @Timed
    //@PreAuthorize("hasAnyAuthority('PERM_GET_CANDIDATE','PERM_GET_CONSULTANT')")
    @GetMapping(produces = APPLICATION_JSON_VALUE, path = "/v1/confirmation-token")
    ResponseEntity<VerificationToken> getConfirmationToken(@RequestParam String token) {
        log.info("get verification token by token = {}", token);
        VerificationToken mailToken = verificationTokenService.getMailToken(token);
        return ResponseEntity.ok().body(mailToken);
    }
}
