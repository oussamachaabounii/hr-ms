package com.talenteo.hr.client;


import com.talenteo.td.dto.TechnicalDocumentDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import springfox.documentation.annotations.ApiIgnore;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RequestMapping("/api")
@FeignClient(name = "td-ms", url = "${td.ms.url}")
@ApiIgnore
public interface TdMsClient {

    @GetMapping(produces = APPLICATION_JSON_VALUE, path = "/v1/technical-documents/by-hr")
    ResponseEntity<TechnicalDocumentDto> getByHrId(@RequestParam(name = "hr_id") Long id);
}
