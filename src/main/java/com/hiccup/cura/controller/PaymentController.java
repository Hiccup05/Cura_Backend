package com.hiccup.cura.controller;

import com.hiccup.cura.security.CustomUser;
import com.hiccup.cura.service.PaymentService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
@RequestMapping("${api.prefix}/payment")
@Slf4j
public class PaymentController {
    private final PaymentService paymentService;

    @PostMapping("/{appointmentId}")
    public ResponseEntity<String> initiatePayment(@PathVariable Long appointmentId, @AuthenticationPrincipal CustomUser user){
        return ResponseEntity.ok(paymentService.initiatePaymentService(appointmentId, user.getId()));
    }

    @GetMapping("/verify")
    public void checkPayment(@RequestParam String pidx, HttpServletResponse response) throws IOException {
        try {
            paymentService.khaltiLookup(pidx);
            response.sendRedirect("http://localhost:3000/payment/success?pidx=" + pidx);
        } catch (Exception e) {
            log.error("Payment verification failed: {}", e.getMessage());
            response.sendRedirect("http://localhost:3000/payment/failed");
        }
    }
}
