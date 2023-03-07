package com.talenteo.hr.converter;

import com.talenteo.hr.dto.VerificationTokenRequest;
import com.talenteo.hr.model.entity.VerificationToken;
import lombok.Data;
import org.springframework.core.convert.converter.Converter;

import java.util.Objects;

@Data(staticConstructor = "newInstance")
public class VerificationTokenRequestConverter implements Converter<VerificationTokenRequest, VerificationToken> {

    @Override
    public VerificationToken convert(VerificationTokenRequest verificationTokenRequest) {
        if (Objects.isNull(verificationTokenRequest)) {
            return null;
        }

        return VerificationToken.builder()
                .confirmationToken(verificationTokenRequest.getConfirmationToken())
                .createdDate(verificationTokenRequest.getCreatedDate())
                .humanResource(HumanResourceDtoConverter.newInstance().convert(verificationTokenRequest.getHumanResource()))
                .build();

    }
}
