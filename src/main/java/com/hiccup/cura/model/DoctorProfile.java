package com.hiccup.cura.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class DoctorProfile {
    @Id
    private Long id;

    @MapsId
    @OneToOne
    private User user;

    @ManyToMany
    @JoinTable(
            name = "doctor_spec",
            joinColumns = @JoinColumn(name = "doctor_id"),
            inverseJoinColumns = @JoinColumn(name = "spec_id")
    )
    private Specialization specialization;

    private int  yearsOfExperience;
    private String licenseNumber;
}
