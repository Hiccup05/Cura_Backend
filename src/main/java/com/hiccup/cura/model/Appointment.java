package com.hiccup.cura.model;

import com.hiccup.cura.enums.AppointmentStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@Entity
public class Appointment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Date createdAt;
    private AppointmentStatus status;
    private Date appointmentTime;
    @JoinColumn(name="created_by")
    @OneToMany
    private User createdBy;
    @OneToMany
    @JoinColumn(name="doctor_id")
    private DoctorProfile doctor;
    @OneToMany
    @JoinColumn(name="staff_id")
    private StaffProfile staffProfile;
    @OneToOne
    @JoinColumn(name="product_id")
    private Product product;
}
