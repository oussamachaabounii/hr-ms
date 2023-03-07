package com.talenteo.hr.client;


import com.talenteo.career.dto.AssessmentCampaignDto;
import com.talenteo.career.dto.BiAnnualAssessmentDto;
import com.talenteo.career.dto.CareerDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import springfox.documentation.annotations.ApiIgnore;

import java.util.List;

import static org.springframework.util.MimeTypeUtils.APPLICATION_JSON_VALUE;

@RequestMapping("/api")
@FeignClient(name = "career-ms", url = "${career.ms.url}")
@ApiIgnore
public interface CareerMsClient {

    @GetMapping(produces = APPLICATION_JSON_VALUE, path = "/v1/careers/{id}")
    public ResponseEntity<CareerDto> getCareer(@PathVariable Long id);

    @GetMapping(produces = APPLICATION_JSON_VALUE, path = "/v1/careers")
    public ResponseEntity<CareerDto> getCareerByResourceId(@RequestParam Long resourceId);

    @GetMapping(produces = APPLICATION_JSON_VALUE, path = "/v1/biannual-assessments/by-hr")
    public ResponseEntity<List<BiAnnualAssessmentDto>> findBiannualAssessmentByhrId(@RequestParam(name = "hr_id") Long hrId);

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE, path = "/v1/campaign-assessments")
    ResponseEntity<List<AssessmentCampaignDto>> getAllAssessmentCampaigns(@RequestParam(name = "company_id") Long companyId);
}
