package com.hiccup.cura.service.payment;

import com.hiccup.cura.dto.response.PaymentInitiateResponse;
import com.hiccup.cura.dto.response.PaymentVerificationResponse;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public interface PaymentStrategy {
    PaymentInitiateResponse initiate(Long appointmentId, Long userId) throws Exception;
    PaymentVerificationResponse verify(Map<String, String> requestParams);
}
