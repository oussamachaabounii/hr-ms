package com.talenteo.hr.converter;

import com.talenteo.hr.dto.VerificationTokenDto;
import com.talenteo.hr.model.entity.VerificationToken;
import lombok.Data;
import org.springframework.core.convert.converter.Converter;

import java.util.Objects;

@Data(staticConstructor = "newInstance")
public class VerificationTokenConverter implements Converter<VerificationToken, VerificationTokenDto> {
    @Override
    public VerificationTokenDto convert(VerificationToken verificationToken) {
        if (Objects.isNull(verificationToken)) {
            return null;
        }
        return VerificationTokenDto.builder()
                .tokenId(verificationToken.getTokenId())
                .confirmationToken(verificationToken.getConfirmationToken())
                .createdDate(verificationToken.getCreatedDate())
                .humanResource(HumanResourceConverter.newInstance().convert(verificationToken.getHumanResource()))
                .build();
    }
}
