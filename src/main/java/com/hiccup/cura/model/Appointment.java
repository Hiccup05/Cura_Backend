package com.hiccup.cura.model;

import com.hiccup.cura.enums.AppointmentStatus;
import com.hiccup.cura.enums.AppointmentType;
import com.hiccup.cura.enums.PaymentMethod;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Appointment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "patient_id")
    private PatientProfile patient;

    @ManyToOne
    @JoinColumn(name = "doctor_id")
    private DoctorProfile doctor;

    @ManyToOne
    @JoinColumn(name = "receptionist_id")
    private ReceptionistProfile receptionist;

    @ManyToOne
    @JoinColumn(name = "service_id")
    private MedicalService medicalService;

    private LocalDate appointmentDate;
    private LocalTime appointmentTime;

    @Enumerated(EnumType.STRING)
    private AppointmentStatus status;

    @Enumerated(EnumType.STRING)
    private AppointmentType type;

    private String reason;

    private String doctorNotes;

    private String walkInPatientName;
    private String walkInPatientPhone;

    private Boolean isPaid;

    @Enumerated(EnumType.STRING)
    private PaymentMethod paymentMethod;

    private LocalDateTime bookedAt;

    @OneToOne(cascade = CascadeType.ALL, mappedBy = "appointment")
    private Prescription prescription;

    @OneToOne(cascade = CascadeType.ALL, mappedBy = "appointment")
    private Payment payment;
}
