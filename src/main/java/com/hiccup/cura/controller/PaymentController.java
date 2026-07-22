package com.hiccup.cura.controller;

import com.hiccup.cura.dto.response.PaymentInitiateResponse;
import com.hiccup.cura.dto.response.PaymentVerificationResponse;
import com.hiccup.cura.enums.PaymentProvider;
import com.hiccup.cura.exception.ErrorResponse;
import com.hiccup.cura.security.CustomUser;
import com.hiccup.cura.service.payment.PaymentFactory;
import com.hiccup.cura.service.payment.PaymentStrategy;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("${api.prefix}/payments")
@RequiredArgsConstructor
@Tag(name="Payment", description = "initiate, verify")
public class PaymentController {

    private final PaymentFactory paymentFactory;

    @Operation(summary = "Start an online payment for an appointment via KHALTI/ESEWA.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Payment initiated; response contains the gateway payment URL to redirect the user to."),
            @ApiResponse(responseCode = "400", description = "Appointment is not payable: already confirmed, cancelled, or receptionist-booked (walk-ins pay at the desk).",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "403", description = "The appointment does not belong to the authenticated patient.",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Appointment not found, or the payment provider is not supported.",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "409", description = "A completed payment already exists for this appointment.",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "502", description = "The payment gateway rejected or failed the initiation request.",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping("/initiate")
    public ResponseEntity<PaymentInitiateResponse> initiatePayment(
            @RequestParam Long appointmentId,
            @AuthenticationPrincipal CustomUser user,
            @RequestParam PaymentProvider provider) throws Exception {

        PaymentStrategy strategy = paymentFactory.createPaymentStrategy(provider);

        PaymentInitiateResponse response = strategy.initiate(appointmentId, user.getId());

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Verify a completed gateway transaction and confirm the appointment (public).")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Verification result; on success the appointment is marked CONFIRMED and paid."),
            @ApiResponse(responseCode = "400", description = "Gateway reported the transaction incomplete, the callback payload/signature is invalid, or the appointment is in a non-payable state.",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Provider not supported, or no payment record matches the gateway's transaction reference.",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "502", description = "The gateway lookup itself failed.",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping("/verify/{provider}")
    public ResponseEntity<PaymentVerificationResponse> verifyPayment(
            @PathVariable PaymentProvider provider,
            @RequestParam Map<String, String> allRequestParams) throws Exception {

        PaymentStrategy strategy = paymentFactory.createPaymentStrategy(provider);

        PaymentVerificationResponse response = strategy.verify(allRequestParams);

        return ResponseEntity.ok(response);
    }
}
