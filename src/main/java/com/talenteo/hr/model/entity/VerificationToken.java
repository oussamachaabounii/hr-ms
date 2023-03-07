package com.talenteo.hr.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Date;
import java.util.UUID;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VerificationToken {

    private static final int EXPIRATION = 60 * 24;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "token_id")
    private Long tokenId;
    private String confirmationToken;
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdDate;
    @OneToOne(targetEntity = HumanResource.class, fetch = FetchType.EAGER)
    private HumanResource humanResource;

    public VerificationToken(HumanResource humanResource) {
        this.humanResource = humanResource;
        createdDate = new Date();
        confirmationToken = UUID.randomUUID().toString();
    }

}
