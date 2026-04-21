package com.hiccup.cura.dto.response;

import com.hiccup.cura.dto.reqeust.PrescriptionRequestDto;
import com.hiccup.cura.enums.AppointmentStatus;
import com.hiccup.cura.enums.AppointmentType;
import com.hiccup.cura.enums.PaymentMethod;
import com.hiccup.cura.model.Prescription;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AppointmentResponseDto {
    private Long id;
    private Long doctorId;
    private String doctorName;
    private Long medicalServiceId;
    private String medicalServiceName;
    private LocalDate appointmentDate;
    private LocalTime appointmentTime;
    private AppointmentStatus status;
    private AppointmentType type;
    private String reason;
    private BigDecimal price;
    private Integer durationMinutes;
    private Boolean isPaid;
    private PaymentMethod paymentMethod;
    private LocalDateTime bookedAt;
    private PrescriptionResponseDto prescriptionResponseDto;

    // Patient booked fields — null for receptionist
    private Long patientId;
    private String patientName;

    // Receptionist booked fields — null for patient
    private Long receptionistId;
    private String receptionistName;
    private String walkInPatientName;
    private String walkInPatientPhone;
}
