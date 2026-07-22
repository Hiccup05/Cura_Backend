package com.hiccup.cura.service.payment.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hiccup.cura.dto.response.EsewaInitiateResponseDto;
import com.hiccup.cura.dto.response.PaymentInitiateResponse;
import com.hiccup.cura.dto.response.PaymentVerificationResponse;
import com.hiccup.cura.enums.AppointmentStatus;
import com.hiccup.cura.enums.PaymentStatus;
import com.hiccup.cura.enums.PaymentType;
import com.hiccup.cura.enums.RoleType;
import com.hiccup.cura.exception.custom.DuplicatePaymentException;
import com.hiccup.cura.exception.custom.InvalidAppointmentException;
import com.hiccup.cura.exception.custom.ResourceNotFoundException;
import com.hiccup.cura.exception.custom.UnauthorizedUserAccessException;
import com.hiccup.cura.model.Appointment;
import com.hiccup.cura.model.Payment;
import com.hiccup.cura.model.User;
import com.hiccup.cura.repository.AppointmentRepository;
import com.hiccup.cura.repository.PaymentRepository;
import com.hiccup.cura.repository.UserRepository;
import com.hiccup.cura.service.payment.PaymentStrategy;
import com.hiccup.cura.util.EsewaSignatureGenerator;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.Clock;
import java.time.OffsetDateTime;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@Component
public class EsewaPaymentStrategy implements PaymentStrategy {

    private final AppointmentRepository appointmentRepository;
    private final UserRepository userRepository;
    private final PaymentRepository paymentRepository;
    @Value("${esewa.product_code}")
    private String productCode;
    private final EsewaSignatureGenerator signatureGenerator;
    private final ObjectMapper objectMapper;
    private final Clock clock;

    @Override
    public PaymentInitiateResponse initiate(Long appointmentId, Long userId) {
        Appointment appointment=appointmentRepository.findById(appointmentId).orElseThrow(() -> new ResourceNotFoundException("Appointment with id " + appointmentId + " not found"));
        validateAppointment(appointment, userId);
        Payment existedPayment = paymentRepository.findByAppointment_IdAndPaymentStatusNotIn(appointmentId, List.of(PaymentStatus.CANCELLED, PaymentStatus.FAILED));
        if(existedPayment!=null && existedPayment.getPaymentStatus().equals(PaymentStatus.COMPLETE)){
            throw new DuplicatePaymentException("Payment is already completed");
        }else if(existedPayment!=null && existedPayment.getPaymentStatus().equals(PaymentStatus.PENDING)){
            if(OffsetDateTime.now(clock).isAfter(existedPayment.getExpiresAt())){
                PaymentInitiateResponse regeneratedPost = PaymentInitiateResponse.builder()
                        .url("https://rc-epay.esewa.com.np/api/epay/main/v2/form")
                        .methodType("POST")
                        .fields(buildEsewaPayload(appointmentId, appointment)).build();

                existedPayment.setExpiresAt(OffsetDateTime.now(clock).plusMinutes(15));
                existedPayment.setPaymentUrl(regeneratedPost.getUrl());
                paymentRepository.save(existedPayment);

                return regeneratedPost;
            }else{
                return PaymentInitiateResponse.builder().url(existedPayment.getPaymentUrl()).methodType("POST").build();
            }
        }
        PaymentInitiateResponse post = PaymentInitiateResponse.builder().url("https://rc-epay.esewa.com.np/api/epay/main/v2/form")
                .methodType("POST")
                .fields(buildEsewaPayload(appointmentId, appointment)).build();
        Payment payment=new Payment();
        payment.setPaymentType(PaymentType.ESEWA);
        payment.setPaymentStatus(PaymentStatus.PENDING);
        payment.setAmount(appointment.getMedicalService().getPrice());
        payment.setAppointment(appointment);
        payment.setPidx(post.getFields().get("transaction_uuid"));
        payment.setPaymentUrl(post.getUrl());
        payment.setExpiresAt(OffsetDateTime.now(clock).plusMinutes(15));
        paymentRepository.save(payment);
        return post;
    }

    @Transactional
    @Override
    public PaymentVerificationResponse verify(Map<String, String> requestParams) throws IOException {
        String data = requestParams.get("data");
        if (data == null || data.isEmpty()) {
            throw new IllegalArgumentException("Missing secure data payload from eSewa.");
        }


        byte[] decodedBytes = Base64.getDecoder().decode(data);
        EsewaInitiateResponseDto responseDto = objectMapper.readValue(decodedBytes, EsewaInitiateResponseDto.class);

        String signatureMessage = "transaction_code=" + responseDto.getTransactionCode() +
                ",status=" + responseDto.getStatus() +
                ",total_amount=" + responseDto.getTotalAmount() +
                ",transaction_uuid=" + responseDto.getTransactionUuid() +
                ",product_code=" + responseDto.getProductCode() +
                ",signed_field_names=" + responseDto.getSignedFieldNames();


        String localSignature = signatureGenerator.getSignature(signatureMessage);
        if (!localSignature.equals(responseDto.getSignature())) {
            throw new SecurityException("eSewa payment verification failed: Signature mismatch.");
        }


        if (!"COMPLETE".equalsIgnoreCase(responseDto.getStatus())) {
            throw new IllegalStateException("Transaction status is not complete: " + responseDto.getStatus());
        }


        Payment payment = paymentRepository.findByPidx(responseDto.getTransactionUuid());
        if (payment == null) {
            throw new ResourceNotFoundException("Payment tracking reference not found for ID: " + responseDto.getTransactionUuid());
        }

        payment.setPaidAt(OffsetDateTime.now(clock));
        payment.setPaymentStatus(PaymentStatus.COMPLETE);
        payment.setTransactionId(responseDto.getTransactionCode());

        Appointment appointment = payment.getAppointment();
        appointment.setStatus(AppointmentStatus.CONFIRMED);
        appointment.setIsPaid(true);

        paymentRepository.save(payment);
        appointmentRepository.save(appointment);

        return PaymentVerificationResponse.builder()
                .status(PaymentStatus.COMPLETE)
                .transactionUuid(responseDto.getTransactionUuid())
                .gatewayTransactionId(responseDto.getTransactionCode())
                .message("Payment verified successfully via eSewa.")
                .build();
    }

    private void validateAppointment(Appointment appointment, Long userId){
        User user = userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("User with id " + userId + " not found"));
        if(user.getRole().stream().anyMatch(role -> role.getName().equals(RoleType.DOCTOR) || role.getName().equals(RoleType.ADMIN))){
            throw new UnauthorizedUserAccessException("User need to be patient to proceed payment of this appointment");
        }
        if (appointment.getPatient()!=null && appointment.getPatient().getId().equals(userId)) {
            if(appointment.getStatus()== AppointmentStatus.CONFIRMED){
                throw new InvalidAppointmentException("Appointment is already confirmed");
            }else if(appointment.getStatus()== AppointmentStatus.CANCELLED){
                throw new InvalidAppointmentException("Appointment is cancelled");
            }
        }else{
            throw new InvalidAppointmentException("Appointment booked by receptionist does not need payment");
        }
    }

    private Map<String, String> buildEsewaPayload(Long appointmentId, Appointment appointment){
        String amountStr = appointment.getMedicalService().getPrice().stripTrailingZeros().toPlainString();
        String transactionUuidStr = String.valueOf(appointmentId);
        
        String signatureData = "total_amount=" + amountStr + ",transaction_uuid=" + transactionUuidStr + ",product_code=" + productCode;
        String generatedSignature = signatureGenerator.getSignature(signatureData);

        return getStringMap(amountStr, transactionUuidStr, generatedSignature);
    }

    private @NonNull Map<String, String> getStringMap(String amountStr, String transactionUuidStr, String generatedSignature) {
        Map<String, String> payload = new HashMap<>();
        payload.put("amount", amountStr);
        payload.put("tax_amount", "0");
        payload.put("product_service_charge", "0");
        payload.put("product_delivery_charge", "0");
        payload.put("total_amount", amountStr);
        payload.put("transaction_uuid", transactionUuidStr);
        payload.put("product_code", productCode);
        payload.put("success_url", "http://localhost:3000/payment/success");
        payload.put("failure_url", "http://localhost:3000/payment/failed");
        payload.put("signed_field_names", "total_amount,transaction_uuid,product_code");
        payload.put("signature", generatedSignature);
        return payload;
    }
}
