package com.talenteo.hr.client;

import com.easyms.mail.domain.Mail;
import com.talenteo.notification.dto.TemplateDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

@RequestMapping("/api")
@FeignClient(name = "notification-ms", url = "${notification.ms.url}")
@ApiIgnore
public interface NotificationClient {


    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE, path = "/v1/templates/send")
    void send(@RequestBody Mail mail);
}
