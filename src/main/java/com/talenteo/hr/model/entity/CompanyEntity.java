package com.talenteo.hr.model.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import javax.persistence.*;
import javax.validation.constraints.Email;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CompanyEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String label;
    @Email
    private String email;
    @Column(name = "phone_number")
    private String phoneNumber;
    @ManyToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "address", referencedColumnName = "id" )
    @JsonBackReference
    @LazyCollection(LazyCollectionOption.FALSE)
    private Address address;
    @ManyToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "company", referencedColumnName = "id")
    @JsonBackReference
    @LazyCollection(LazyCollectionOption.FALSE)
    private Company company;
    private Boolean isActive;

}
