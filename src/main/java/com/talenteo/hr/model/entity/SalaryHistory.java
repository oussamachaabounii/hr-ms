package com.talenteo.hr.model.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.*;
import lombok.experimental.SuperBuilder;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "salary_history")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SalaryHistory {
    @Id
    @GeneratedValue
    private Long id;
    private double salaryAmount;
    private String currency;
    private Date startDate;
    private Date endDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hr_id", referencedColumnName = "id")
    @JsonBackReference
    private HumanResource hr;

}
