package com.hiccup.cura.controller;

import com.hiccup.cura.dto.response.PaymentInitiateResponse;
import com.hiccup.cura.dto.response.PaymentVerificationResponse;
import com.hiccup.cura.enums.PaymentProvider;
import com.hiccup.cura.security.CustomUser;
import com.hiccup.cura.service.payment.PaymentFactory;
import com.hiccup.cura.service.payment.PaymentStrategy;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentFactory paymentFactory;

    @PostMapping("/initiate")
    public ResponseEntity<PaymentInitiateResponse> initiatePayment(
            @RequestParam Long appointmentId,
            @AuthenticationPrincipal CustomUser user,
            @RequestParam PaymentProvider provider) throws Exception {

        PaymentStrategy strategy = paymentFactory.createPaymentStrategy(provider);

        PaymentInitiateResponse response = strategy.initiate(appointmentId, user.getId());

        return ResponseEntity.ok(response);
    }


    @PostMapping("/verify/{provider}")
    public ResponseEntity<PaymentVerificationResponse> verifyPayment(
            @PathVariable PaymentProvider provider,
            @RequestParam Map<String, String> allRequestParams) throws Exception {

        PaymentStrategy strategy = paymentFactory.createPaymentStrategy(provider);

        PaymentVerificationResponse response = strategy.verify(allRequestParams);

        return ResponseEntity.ok(response);
    }
}
