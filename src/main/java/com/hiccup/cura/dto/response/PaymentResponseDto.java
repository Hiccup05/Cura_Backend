package com.hiccup.cura.dto.response;

import com.hiccup.cura.enums.AppointmentStatus;
import com.hiccup.cura.enums.PaymentStatus;
import com.hiccup.cura.enums.PaymentType;
import com.hiccup.cura.model.Appointment;
import jakarta.persistence.OneToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PaymentResponseDto {
    private Long id;

    private PaymentType paymentType;

    private PaymentStatus paymentStatus;

    private BigDecimal amount;

    private String pidx;

    private String transactionId;

    private LocalDateTime paidAt;

    private Long appointmentId;
}
