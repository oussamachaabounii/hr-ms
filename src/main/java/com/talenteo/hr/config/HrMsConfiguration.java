package com.talenteo.hr.config;

import com.talenteo.common.utils.AzureBlobStorageUtils;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

@Configuration
@ComponentScan(basePackages = {"com.talenteo.hr", "com.easyms.common","com.easyms.security", "com.talenteo.common"})
@EnableJpaRepositories(basePackages = {"com.talenteo.hr.repository"})
@EnableFeignClients(basePackages = "com.talenteo.hr.client")
@EnableWebSecurity
public class HrMsConfiguration {
    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Inject
    private AzureBlobStorageUtils azureBlobStorageUtils;

    @Inject
    private HrProperties hrProperties;

    @PostConstruct
    private void postConstruct() {
        // TODO import from app constant
        azureBlobStorageUtils.initClient(hrProperties.getClientId(), hrProperties.getClientSecret(), hrProperties.getTenantId(),
                hrProperties.getEndpoint());
    }
}
