package com.talenteo.hr.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import springfox.documentation.annotations.ApiIgnore;

import java.util.Map;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RequestMapping("/api")
@FeignClient(name = "mission-ms", url = "${mission.ms.url}")
@ApiIgnore
public interface MissionMsClient {

    @GetMapping(produces = APPLICATION_JSON_VALUE, path = "/v1/missions/consultants/stats/{id}")
    ResponseEntity<Map<String, Object>> getMissionsStatsByConsultant(@PathVariable Long id);
}
