package com.hiccup.cura.dto.reqeust;

import com.hiccup.cura.enums.AppointmentStatus;
import com.hiccup.cura.enums.AppointmentType;
import com.hiccup.cura.enums.PaymentMethod;
import com.hiccup.cura.model.DoctorProfile;
import com.hiccup.cura.model.MedicalService;
import com.hiccup.cura.model.PatientProfile;
import com.hiccup.cura.model.ReceptionistProfile;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AppointmentRequestDto {
    private Long doctorId;

    private Long receptionistId;

    private Long medicalServiceId;

    private LocalDate appointmentDate;

    private LocalTime appointmentTime;

    private AppointmentStatus status;

    private String reason;

    private String doctorNotes;

    private String walkInPatientName;

    private String walkInPatientPhone;

    private PaymentMethod paymentMethod;
}
