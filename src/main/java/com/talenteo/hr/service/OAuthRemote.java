package com.talenteo.hr.service;

import com.talenteo.common.security.SecurityUtils;
import com.talenteo.hr.client.OAuthClient;
import com.talenteo.hr.dto.OauthUserDto;
import com.talenteo.hr.model.entity.HumanResource;
import com.talenteo.hr.model.entity.VerificationToken;
import com.talenteo.hr.repository.VerificationTokenRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Calendar;
import java.util.Objects;

@Slf4j
@Component
@AllArgsConstructor
public class OAuthRemote {

    private final OAuthClient oAuthClient;

    private final VerificationTokenRepository verificationTokenRepository;

    public OauthUserDto createOAuthUser(OauthUserDto user) {
        return oAuthClient.createUser(user);
    }


    public OauthUserDto getOAuthUser() {
        Authentication currentAuthentication = SecurityContextHolder.getContext().getAuthentication();
        return oAuthClient.getByLogin(SecurityUtils.getCurrentUserId());
    }

    public OauthUserDto updateOAuthUser(String id, String password) {
        return oAuthClient.updateUser(id, password);
    }

    public OauthUserDto updateOAuthUser2(String id, String password) {
        return oAuthClient.updateUser2(id, password);
    }

    public OauthUserDto getOAuthUser(String id) {
        return oAuthClient.getById(id).getBody();
    }

    public void deleteOAuthUser(Long id) {
        oAuthClient.deleteUser(id);
    }

    public HumanResource confirmRegistration(String token) {
        VerificationToken verificationToken = verificationTokenRepository.findByConfirmationToken(token);
        HumanResource humanResource = verificationToken.getHumanResource();
        Calendar calendar = Calendar.getInstance();
        if ((verificationToken.getCreatedDate().getTime() - calendar.getTime().getTime()) <= 0) {
            OauthUserDto user = this.getOAuthUser(humanResource.getId().toString());
        }
        return Objects.nonNull(verificationToken) ? verificationToken.getHumanResource() : null;
    }
}
