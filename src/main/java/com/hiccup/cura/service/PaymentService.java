package com.hiccup.cura.service;

import com.hiccup.cura.dto.reqeust.KhaltiRequestDto;
import com.hiccup.cura.dto.response.KhaltiLookupResponseDto;
import com.hiccup.cura.dto.response.KhaltiResposneDto;
import com.hiccup.cura.dto.response.PaymentResponseDto;
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
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@Service
public class PaymentService {
    private final AppointmentRepository appointmentRepository;
    private final PaymentRepository paymentRepository;
    private final UserRepository userRepository;
    private final WebClient webClient;
    private final EmailService emailService;

    @Transactional
    public String initiatePaymentService(Long appointmentId, Long userId) {
        Appointment appointment=appointmentRepository.findById(appointmentId).orElseThrow(() -> new ResourceNotFoundException("Appointment with id " + appointmentId + " not found"));
        validateAppointment(appointment, userId);
        Payment existedPayment = paymentRepository.findByAppointment_IdAndPaymentStatusNotIn(appointmentId, List.of(PaymentStatus.CANCELLED, PaymentStatus.FAILED));
        if(existedPayment!=null && existedPayment.getPaymentStatus().equals(PaymentStatus.COMPLETE)){
            throw new DuplicatePaymentException("Payment is already completed");
        }else if(existedPayment!=null && existedPayment.getPaymentStatus().equals(PaymentStatus.PENDING)){
            if(OffsetDateTime.now().isAfter(existedPayment.getExpiresAt())){
                KhaltiResposneDto response = getKhaltiResponse(appointmentId, userId, appointment);
                existedPayment.setExpiresAt(response.getExpiresAt());
                existedPayment.setPidx(response.getPidx());
                existedPayment.setPaymentUrl(response.getPaymentUrl());
                paymentRepository.save(existedPayment);
                return response.getPaymentUrl();
            }else{
                return existedPayment.getPaymentUrl();
            }
        }

        KhaltiResposneDto response = getKhaltiResponse(appointmentId, userId,  appointment);
        Payment payment=new Payment();
        payment.setPaymentType(PaymentType.KHALTI);
        payment.setPaymentStatus(PaymentStatus.PENDING);
        payment.setAmount(appointment.getMedicalService().getPrice());
        payment.setAppointment(appointment);
        payment.setPidx(response.getPidx());
        payment.setPaymentUrl(response.getPaymentUrl());
        payment.setExpiresAt(response.getExpiresAt());
        paymentRepository.save(payment);
        return response.getPaymentUrl();
    }

    @Transactional
    public PaymentResponseDto khaltiLookup(String pidx){
        KhaltiLookupResponseDto block = webClient.post()
                .uri("epayment/lookup/")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(Map.of("pidx",pidx))
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(KhaltiLookupResponseDto.class)
                .block();
        if("Completed".equals(block.getStatus())){
            Payment payment = paymentRepository.findByPidx(block.getPidx());
            payment.setPaymentStatus(PaymentStatus.COMPLETE);
            payment.setTransactionId(block.getTransactionId());
            payment.setPaidAt(LocalDateTime.now());
            Appointment appointment = payment.getAppointment();
            appointment.setStatus(AppointmentStatus.CONFIRMED);
            appointment.setIsPaid(true);
            appointmentRepository.save(appointment);
            paymentRepository.save(payment);
            emailService.sendPaymentSuccessEmail(appointment.getPatient().getUser().getEmail(), appointment);
            return mapToDto(payment);
        }else{
            throw new KhaltiGatewayFailException("Cannot complete payment");
        }
    }

    public KhaltiResposneDto getKhaltiResponse(Long appointmentId, Long userId, Appointment appointment) {
        KhaltiRequestDto khaltiRequestDto = new KhaltiRequestDto();
        khaltiRequestDto.setPurchaseOrderId(appointmentId);
        khaltiRequestDto.setAmount(appointment.getMedicalService().getPrice().multiply(BigDecimal.valueOf(100)));
        khaltiRequestDto.setWebsiteUrl("https://lincoln-dittographic-unenergetically.ngrok-free.dev");
        khaltiRequestDto.setReturnUrl("https://lincoln-dittographic-unenergetically.ngrok-free.dev/api/v1/payment/verify");
        khaltiRequestDto.setPurchaseOrderName(userId+" "+appointmentId);

        KhaltiResposneDto response = webClient.post()
                .uri("epayment/initiate/")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(khaltiRequestDto)
                .retrieve()
                .bodyToMono(KhaltiResposneDto.class)
                .block();

        if(response == null){
            throw new KhaltiGatewayFailException("Khalti payment gateway fail");
        }
        return response;
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

    private PaymentResponseDto mapToDto(Payment payment){
        return PaymentResponseDto.builder()
                .id(payment.getId())
                .paymentStatus(payment.getPaymentStatus())
                .paymentType(payment.getPaymentType())
                .amount(payment.getAmount())
                .paidAt(payment.getPaidAt())
                .appointmentId(payment.getAppointment().getId())
                .build();
    }
}
