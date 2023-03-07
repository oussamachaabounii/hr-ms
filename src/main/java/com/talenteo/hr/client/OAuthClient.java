package com.talenteo.hr.client;


import com.talenteo.hr.dto.OauthUserDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import static org.springframework.util.MimeTypeUtils.APPLICATION_JSON_VALUE;

@RequestMapping("/api")
@FeignClient(name = "talenteo-oauth2", url = "${oauth2.ms.url}")
@ApiIgnore
public interface OAuthClient {

    @PostMapping(consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE, path = "/v1/users")
    OauthUserDto createUser(@RequestBody OauthUserDto user);

    @GetMapping(consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE, path = "/v1/users/login/{login}")
    OauthUserDto getByLogin(@PathVariable String login);

    @GetMapping(consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE, path = "/v1/users/{id}")
    ResponseEntity<OauthUserDto> getById(@PathVariable String id);

    @RequestMapping(produces = MediaType.APPLICATION_JSON_VALUE, method = {RequestMethod.PUT}, value = "/v1/users/{id}/{password}")
    OauthUserDto updateUser(@PathVariable String id, @PathVariable String password);

    @RequestMapping(produces = MediaType.APPLICATION_JSON_VALUE, method = {RequestMethod.PUT}, value = "/v1/users/{password}")
    OauthUserDto updateUser2(@RequestParam String id, @PathVariable String password);
    @DeleteMapping(consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE, path = "/v1/users/{id}")
    void deleteUser(@PathVariable Long id);
}
