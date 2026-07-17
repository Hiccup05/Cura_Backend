package com.hiccup.cura.model;

import com.hiccup.cura.enums.BloodGroup;
import com.hiccup.cura.enums.Gender;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PatientProfile {

    @Id
    private Long id;
    private String firstName;
    private String lastName;

    private LocalDate dateOfBirth;

    @Enumerated(EnumType.STRING)
    private Gender gender;

    private String phoneNumber;
    private String address;

    @Enumerated(EnumType.STRING)
    private BloodGroup bloodGroup;

    private String allergies;
    private String chronicConditions;
    private String emergencyContactName;
    private String emergencyContactPhone;

    @MapsId
    @OneToOne(cascade = CascadeType.ALL)
    private User user;
}
