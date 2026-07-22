package com.hiccup.cura.service.payment.impl;

import com.hiccup.cura.dto.request.KhaltiRequestDto;
import com.hiccup.cura.dto.response.KhaltiLookupResponseDto;
import com.hiccup.cura.dto.response.KhaltiResponseDto;
import com.hiccup.cura.dto.response.PaymentInitiateResponse;
import com.hiccup.cura.dto.response.PaymentVerificationResponse;
import com.hiccup.cura.enums.AppointmentStatus;
import com.hiccup.cura.enums.PaymentStatus;
import com.hiccup.cura.enums.PaymentType;
import com.hiccup.cura.enums.RoleType;
import com.hiccup.cura.exception.custom.*;
import com.hiccup.cura.model.Appointment;
import com.hiccup.cura.model.Payment;
import com.hiccup.cura.model.User;
import com.hiccup.cura.repository.AppointmentRepository;
import com.hiccup.cura.repository.PaymentRepository;
import com.hiccup.cura.repository.UserRepository;
import com.hiccup.cura.service.EmailService;
import com.hiccup.cura.service.payment.PaymentStrategy;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@Component
public class KhaltiPaymentStrategy implements PaymentStrategy {
    private final AppointmentRepository appointmentRepository;
    private final PaymentRepository paymentRepository;
    private final UserRepository userRepository;
    private final WebClient webClient;
    private final EmailService emailService;
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
                KhaltiResponseDto response = getKhaltiResponse(appointmentId, userId, appointment);
                existedPayment.setExpiresAt(response.getExpiresAt());
                existedPayment.setPidx(response.getPidx());
                existedPayment.setPaymentUrl(response.getPaymentUrl());
                paymentRepository.save(existedPayment);
                return PaymentInitiateResponse.builder().url(response.getPaymentUrl()).methodType("GET").build();
            }else{
                return PaymentInitiateResponse.builder().url(existedPayment.getPaymentUrl()).methodType("GET").build();
            }
        }

        KhaltiResponseDto response = getKhaltiResponse(appointmentId, userId,  appointment);
        Payment payment=new Payment();
        payment.setPaymentType(PaymentType.KHALTI);
        payment.setPaymentStatus(PaymentStatus.PENDING);
        payment.setAmount(appointment.getMedicalService().getPrice());
        payment.setAppointment(appointment);
        payment.setPidx(response.getPidx());
        payment.setPaymentUrl(response.getPaymentUrl());
        payment.setExpiresAt(response.getExpiresAt());
        paymentRepository.save(payment);
        return  PaymentInitiateResponse.builder().url(response.getPaymentUrl()).methodType("GET").build();
    }

    @Override
    public PaymentVerificationResponse verify(Map<String, String> requestParams) {
        KhaltiLookupResponseDto block = webClient.post()
                .uri("epayment/lookup/")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(Map.of("pidx", requestParams.get("pidx")))
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(KhaltiLookupResponseDto.class)
                .block();

        PaymentStatus unifiedStatus;
        String message = "";

        if (block != null && "Completed".equals(block.getStatus())) {
            unifiedStatus = PaymentStatus.COMPLETE;

            Payment payment = paymentRepository.findByPidx(block.getPidx());
            payment.setPaymentStatus(PaymentStatus.COMPLETE);
            payment.setTransactionId(block.getTransactionId());
            payment.setPaidAt(OffsetDateTime.now(clock));

            Appointment appointment = payment.getAppointment();
            appointment.setStatus(AppointmentStatus.CONFIRMED);
            appointment.setIsPaid(true);

            appointmentRepository.save(appointment);
            paymentRepository.save(payment);
            emailService.sendPaymentSuccessEmail(appointment.getPatient().getUser().getEmail(), appointment);
        } else {
            throw new KhaltiGatewayFailException("Cannot complete payment");
        }

        return PaymentVerificationResponse.builder()
                .status(unifiedStatus)
                .message(message)
                .transactionUuid(block.getPidx())
                .gatewayTransactionId(block.getTransactionId())
                .build();
    }

    private KhaltiResponseDto getKhaltiResponse(Long appointmentId, Long userId, Appointment appointment) {
        KhaltiRequestDto khaltiRequestDto = getKhaltiRequestDto(appointmentId, userId, appointment);

        KhaltiResponseDto response = webClient.post()
                .uri("epayment/initiate/")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(khaltiRequestDto)
                .retrieve()
                .bodyToMono(KhaltiResponseDto.class)
                .block();

        if(response == null){
            throw new KhaltiGatewayFailException("Khalti payment gateway fail");
        }
        return response;
    }

    private static @NonNull KhaltiRequestDto getKhaltiRequestDto(Long appointmentId, Long userId, Appointment appointment) {
        KhaltiRequestDto khaltiRequestDto = new KhaltiRequestDto();
        khaltiRequestDto.setPurchaseOrderId(appointmentId);
        khaltiRequestDto.setAmount(appointment.getMedicalService().getPrice().multiply(BigDecimal.valueOf(100)));
        khaltiRequestDto.setWebsiteUrl("https://lincoln-dittographic-unenergetically.ngrok-free.dev");
        khaltiRequestDto.setReturnUrl("https://lincoln-dittographic-unenergetically.ngrok-free.dev/api/v1/payments/verify/KHALTI");
        khaltiRequestDto.setPurchaseOrderName(userId +" "+ appointmentId);
        return khaltiRequestDto;
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
        }else if(appointment.getReceptionist()!=null && appointment.getReceptionist().getId().equals(userId)){
            throw new InvalidAppointmentException("Appointment booked by receptionist does not need payment");
        }
    }
}
