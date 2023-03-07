package com.talenteo.hr.client;


import com.easyms.mail.domain.Mail;
import com.talenteo.hr.model.entity.HumanResource;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import springfox.documentation.annotations.ApiIgnore;


@FeignClient(name = "search-ms", url = "${search.ms.url}")
@ApiIgnore
public interface SearchMsClient {

    @PutMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE, path = "/index-human-resource")
    void indexHumanResource(@RequestBody HumanResource humanResource);

}
