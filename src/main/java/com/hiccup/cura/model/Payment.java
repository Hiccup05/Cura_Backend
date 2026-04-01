package com.hiccup.cura.model;

import com.hiccup.cura.enums.PaymentStatus;
import com.hiccup.cura.enums.PaymentType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private PaymentType paymentType;

    private PaymentStatus paymentStatus;

    private BigDecimal amount;

    private String pidx;

    private String transactionId;

    private LocalDateTime paidAt;

    @OneToOne
    private Appointment appointment;
}
