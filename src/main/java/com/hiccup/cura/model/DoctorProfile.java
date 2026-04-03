package com.hiccup.cura.model;

import com.hiccup.cura.enums.BloodGroup;
import com.hiccup.cura.enums.DoctorStatus;
import com.hiccup.cura.enums.Gender;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class DoctorProfile {
    @Id
    private Long id;

    private String firstName;
    private String lastName;

    private LocalDate dateOfBirth;

    @Enumerated(EnumType.STRING)
    private Gender gender;

    private String phoneNumber;
    private String address;

    @MapsId
    @OneToOne(cascade = CascadeType.ALL)
    private User user;

    @Enumerated(EnumType.STRING)
    @ManyToMany
    @JoinTable(
            name = "doctor_spec",
            joinColumns = @JoinColumn(name = "doctor_id"),
            inverseJoinColumns = @JoinColumn(name = "spec_id")
    )
    private Set<Specialization> specialization;

    private int  yearsOfExperience;
    private String licenseNumber;

    @Enumerated(EnumType.STRING)
    private DoctorStatus doctorStatus;
}
