package com.hiccup.cura.dto.request;

import com.hiccup.cura.enums.PaymentMethod;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AppointmentRequestDto {
    @NotNull(message = "doctorId is required")
    private Long doctorId;

    private Long receptionistId;

    @NotNull(message = "medicalServiceId is required")
    private Long medicalServiceId;

    @NotNull(message = "appointmentDate is required")
    private LocalDate appointmentDate;

    @NotNull(message = "appointmentTime is required")
    private LocalTime appointmentTime;

    private String reason;

    private String walkInPatientName;

    private String walkInPatientPhone;

    private PaymentMethod paymentMethod;
}
