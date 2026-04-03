package com.hiccup.cura.controller;

import com.hiccup.cura.dto.response.PaymentResponseDto;
import com.hiccup.cura.security.CustomUser;
import com.hiccup.cura.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("${api.prefix}/payment")
public class PaymentController {
    private final PaymentService paymentService;

    @PostMapping("/{appointmentId}")
    public ResponseEntity<String> initiatePayment(@PathVariable Long appointmentId, @AuthenticationPrincipal CustomUser user){
        return ResponseEntity.ok(paymentService.initiatePaymentService(appointmentId, user.getId()));
    }

    @GetMapping("/verify")
    public ResponseEntity<PaymentResponseDto> checkPayment(@RequestParam String pidx){
        return ResponseEntity.ok(paymentService.khaltiLookup(pidx));
    }
}
