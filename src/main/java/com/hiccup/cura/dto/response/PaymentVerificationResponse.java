package com.hiccup.cura.dto.response;

import com.hiccup.cura.enums.PaymentStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PaymentVerificationResponse {
    private PaymentStatus status;
    private String transactionUuid;
    private String gatewayTransactionId;
    private String message;
}
