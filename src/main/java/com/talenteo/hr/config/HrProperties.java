package com.talenteo.hr.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
public class HrProperties {

    @Value("${front.url}")
    private String frontUrl;

    @Value("${front.mail.confirm.url}")
    private String routeMailConfirm;

    @Value("${azure-storage.client-id}")
    private String clientId;
    @Value("${azure-storage.client-secret}")
    private String clientSecret;
    @Value("${azure-storage.tenant-id}")
    private String tenantId;
    @Value("${azure-storage.endpoint}")
    private String endpoint;

}
