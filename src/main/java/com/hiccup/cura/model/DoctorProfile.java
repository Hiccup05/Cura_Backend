package com.hiccup.cura.model;

import com.hiccup.cura.enums.DoctorStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name="doctor_profile")
public class DoctorProfile {
    private Long id;
    private String specialization;
    @Enumerated(EnumType.STRING)
    private DoctorStatus doctorStatus;
    private String licenseNumber;
    private String experience;
    @JoinColumn(name="user_id")
    private User user;
}
