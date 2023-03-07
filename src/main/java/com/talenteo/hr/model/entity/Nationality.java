package com.talenteo.hr.model.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "nationality")
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class Nationality {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Column
    private String country;

    @ManyToMany(mappedBy = "nationalities")
    @JsonBackReference
    @ToString.Exclude
    private List<HumanResource> humanResources = new ArrayList<>();
}
