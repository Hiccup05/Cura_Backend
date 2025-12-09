package com.hiccup.cura.model;

import com.hiccup.cura.enums.BloodGroup;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name="patient_profile")
public class PatientProfile {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Enumerated(EnumType.STRING)
    private BloodGroup bloodGroup;
    private String emergencyNumber;
    @JoinColumn(name="user_id")
    private User user;
}
