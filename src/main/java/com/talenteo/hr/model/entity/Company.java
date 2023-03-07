package com.talenteo.hr.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Builder
public class Company {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String label;
    private String logo;
    @Column(name = "phone_number")
    private String phoneNumber;
    private String description;
    private String size;
    private String type;
    @Column(name = "creation_date")
    private Date creationDate;
    @Column(name = "activity_field")
    private String activityField;
 
    private Boolean isActive;
}
