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
public class Appointment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "patient_id")
    private PatientProfile patient; //done

    @ManyToOne
    @JoinColumn(name = "doctor_id")
    private DoctorProfile doctor;  //dpmne

    @ManyToOne
    @JoinColumn(name = "receptionist_id")
    private ReceptionistProfile receptionist;  //done

    @ManyToOne
    @JoinColumn(name = "service_id")
    private MedicalService medicalService;  //done

    private LocalDate appointmentDate;  //done
    private LocalTime appointmentTime;  //done

    @Enumerated(EnumType.STRING)
    private AppointmentStatus status;  //done

    @Enumerated(EnumType.STRING)
    private AppointmentType type;  //done

    private String reason;     //
    private String doctorNotes;  // done

    private String walkInPatientName;  //done
    private String walkInPatientPhone;  //done

    private Boolean isPaid;  //done

    @Enumerated(EnumType.STRING)
    private PaymentMethod paymentMethod; //done

    private LocalDateTime bookedAt;  //done
}
