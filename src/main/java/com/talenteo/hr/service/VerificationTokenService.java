package com.talenteo.hr.service;

import com.talenteo.hr.model.entity.VerificationToken;
import com.talenteo.hr.repository.VerificationTokenRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@AllArgsConstructor
public class VerificationTokenService {
    private final VerificationTokenRepository verificationTokenRepository;

    public VerificationToken getMailToken(String token) {
        VerificationToken verificationToken = verificationTokenRepository.findByConfirmationToken(token);
        return verificationToken;
    }
}
