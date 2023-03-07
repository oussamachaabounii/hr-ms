package com.talenteo.hr.model.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.talenteo.hr.dto.Gender;
import com.talenteo.hr.dto.Role;
import com.talenteo.hr.dto.Visibility;
import lombok.*;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import javax.persistence.*;
import javax.validation.constraints.Email;
import java.util.Date;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HumanResource {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String lastname;
    private String firstname;
    @Email
    private String email;
    @Enumerated(EnumType.STRING)
    private Gender gender;
    private String avatar;
    @Column(name = "birth_date")
    private Date birthDate;
    @Column(name = "phone_number")
    private String phoneNumber;
    @ManyToMany(cascade = {CascadeType.ALL}, fetch = FetchType.EAGER)
    @JoinTable(name = "hr_nationality",
            joinColumns = @JoinColumn(name = "hr_id"),
            inverseJoinColumns = @JoinColumn(name = "nationality_id")
    )
    //@JsonIgnore
    @ToString.Exclude
    @JsonManagedReference
    private List<Nationality> nationalities;
    @Enumerated(EnumType.STRING)
    private Role role;

    @ManyToOne(cascade=CascadeType.ALL)
    @JoinColumn(name = "address", referencedColumnName = "id")
    private Address address;
    @ManyToOne
    private HumanResource supervisor;
    @LazyCollection(LazyCollectionOption.FALSE)
    @JoinColumn(name = "company_entity_id", referencedColumnName = "id")
    @ManyToOne(cascade=CascadeType.MERGE)
    @JsonBackReference
    private CompanyEntity companyEntity;
    @Enumerated(EnumType.STRING)
    private Visibility visibility;
    private boolean alreadyLoggedIn = false;

    @LazyCollection(LazyCollectionOption.FALSE)
    @OneToMany( cascade = CascadeType.ALL, mappedBy = "hr")
    @JsonManagedReference
    @ToString.Exclude
    private List<SalaryHistory> salaryHistory;
    private boolean isActive;
}
