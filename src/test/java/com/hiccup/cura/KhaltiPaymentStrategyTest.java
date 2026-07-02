package com.hiccup.cura;

import com.hiccup.cura.dto.response.KhaltiLookupResponseDto;
import com.hiccup.cura.dto.response.PaymentVerificationResponse;
import com.hiccup.cura.enums.AppointmentStatus;
import com.hiccup.cura.enums.PaymentStatus;
import com.hiccup.cura.exception.custom.DuplicatePaymentException;
import com.hiccup.cura.exception.custom.KhaltiGatewayFailException;
import com.hiccup.cura.model.*;
import com.hiccup.cura.repository.AppointmentRepository;
import com.hiccup.cura.repository.PaymentRepository;
import com.hiccup.cura.repository.UserRepository;
import com.hiccup.cura.service.EmailService;
import com.hiccup.cura.service.payment.impl.KhaltiPaymentStrategy;
import com.hiccup.cura.util.TestDataFactory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Answers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class KhaltiPaymentStrategyTest {

    @Mock
    private AppointmentRepository appointmentRepository;

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private WebClient webClient;

    @Mock
    private WebClient.RequestBodyUriSpec requestBodyUriSpec;

    @Mock
    private WebClient.RequestBodySpec requestBodySpec;

    @Mock
    private WebClient.RequestHeadersSpec requestHeadersSpec;

    @Mock
    private WebClient.ResponseSpec responseSpec;

    @Mock
    private Mono<KhaltiLookupResponseDto> khaltiMono;

    @Mock
    private EmailService emailService;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private KhaltiPaymentStrategy paymentStrategy;

    @Test
    @DisplayName("initiate() should throw DuplicatePaymentException when a completed payment already exists")
    void verifyDuplicatePaymentException() {
        // 1. ARRANGE
        Long appointmentId = 100L;
        Long userId = 1L;

        // Bring in your fake data from your factory
        // Ensure user matches patient ID and role is a PATIENT to pass validateAppointment()
        PatientProfile patient=TestDataFactory.createPatientProfile();

        User patientUser=TestDataFactory.createPatient();



        // FIX 1: Set status to PENDING so it slides right past validateAppointment() safely
        Appointment appointment = new Appointment();
        appointment.setId(appointmentId);
        appointment.setStatus(AppointmentStatus.PENDING);
        appointment.setPatient(patient);
        appointment.setMedicalService(MedicalService.builder().price(BigDecimal.valueOf(1000)).build()); // Avoid NullPointerException on price lookup

        // Create a payment that is already COMPLETE
        Payment completedPayment = new Payment();
        completedPayment.setPaymentStatus(PaymentStatus.COMPLETE);

        // FIX 2: Teach your mocks exactly how to respond when production code queries them
        Mockito.when(appointmentRepository.findById(appointmentId))
                .thenReturn(Optional.of(appointment));

        Mockito.when(userRepository.findById(userId))
                .thenReturn(Optional.of(patientUser));

        Mockito.when(paymentRepository.findByAppointment_IdAndPaymentStatusNotIn(
                        appointmentId,
                        List.of(PaymentStatus.CANCELLED, PaymentStatus.FAILED)))
                .thenReturn(completedPayment);

        // 2 & 3. ACT & ASSERT
        // FIX 3: Pass arguments into the method call inside the assertion
        DuplicatePaymentException exception = assertThrows(DuplicatePaymentException.class, () -> {
            paymentStrategy.initiate(appointmentId, userId);
        });

        // Final check to prove the exception message matches exactly what you wrote in production
        assertEquals("Payment is already completed", exception.getMessage());

        // Extra Engineering Check: Verify that emailService was NEVER called (since payment failed early)
        Mockito.verifyNoInteractions(emailService);
    }

    @Test
    @DisplayName("verify() should throw KhaltiGatewayFailException when gateway status is not Completed")
    void verify_GatewayReturnsFailure_ThrowsKhaltiGatewayFailException() {
        // 1. ARRANGE
        Map<String, String> requestParams = Map.of("pidx", "fake_khalti_pidx_123");

        KhaltiLookupResponseDto mockFailedResponse = new KhaltiLookupResponseDto();
        mockFailedResponse.setPidx("fake_khalti_pidx_123");
        mockFailedResponse.setStatus("FAILED");

        // Connect the links of the chain one by one
        Mockito.when(webClient.post()).thenReturn(requestBodyUriSpec);

        Mockito.when(requestBodyUriSpec.uri(Mockito.anyString())).thenReturn(requestBodySpec);

        Mockito.when(requestBodySpec.contentType(Mockito.any(org.springframework.http.MediaType.class)))
                .thenReturn(requestBodySpec);

        Mockito.when(requestBodySpec.bodyValue(Mockito.any()))
                .thenReturn(requestHeadersSpec);

        // FIX: Explicitly handle the varargs array matcher that was causing the NullPointerException
        Mockito.when(requestHeadersSpec.accept(Mockito.any(org.springframework.http.MediaType[].class)))
                .thenReturn(requestHeadersSpec);

        Mockito.when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);

        Mockito.when(responseSpec.bodyToMono(KhaltiLookupResponseDto.class)).thenReturn(khaltiMono);

        Mockito.when(khaltiMono.block()).thenReturn(mockFailedResponse);

        // 2 & 3. ACT & ASSERT
        assertThrows(KhaltiGatewayFailException.class, () -> {
            paymentStrategy.verify(requestParams);
        });

        // Verify side-effects remain untouched
        Mockito.verifyNoInteractions(paymentRepository);
        Mockito.verifyNoInteractions(appointmentRepository);
    }


    @Test
    @DisplayName("verify() should successfully confirm appointment when gateway status is Completed")
    void verify_SuccessPath_UpdatesDatabaseAndReturnsResponse() {
        // 1. ARRANGE
        Map<String, String> requestParams = Map.of("pidx", "fake_khalti_pidx_123");

        // Gateway response is successful
        KhaltiLookupResponseDto mockSuccessResponse = new KhaltiLookupResponseDto();
        mockSuccessResponse.setPidx("fake_khalti_pidx_123");
        mockSuccessResponse.setStatus("Completed");

        // Set up your domain models for the success flow
        PatientProfile patient = TestDataFactory.createPatientProfile();
        Appointment appointment = new Appointment();
        appointment.setId(100L);
        appointment.setStatus(AppointmentStatus.PENDING);
        appointment.setPatient(patient);

        Payment pendingPayment = new Payment();
        pendingPayment.setPaymentStatus(PaymentStatus.PENDING);
        pendingPayment.setAppointment(appointment);

        // The WebClient chain stubbing (Your flawless setup!)
        Mockito.when(webClient.post()).thenReturn(requestBodyUriSpec);
        Mockito.when(requestBodyUriSpec.uri(Mockito.anyString())).thenReturn(requestBodySpec);
        Mockito.when(requestBodySpec.contentType(Mockito.any(org.springframework.http.MediaType.class))).thenReturn(requestBodySpec);
        Mockito.when(requestBodySpec.bodyValue(Mockito.any())).thenReturn(requestHeadersSpec);
        Mockito.when(requestHeadersSpec.accept(Mockito.any(org.springframework.http.MediaType[].class))).thenReturn(requestHeadersSpec);
        Mockito.when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        Mockito.when(responseSpec.bodyToMono(KhaltiLookupResponseDto.class)).thenReturn(khaltiMono);
        Mockito.when(khaltiMono.block()).thenReturn(mockSuccessResponse);

        // Teach the database to find your pending payment transaction
        // (Adjust the method name below to match your actual Repository lookup method)
        Mockito.when(paymentRepository.findByPidx("fake_khalti_pidx_123"))
                .thenReturn(pendingPayment);

        // 2. ACT
        // Call the ACTUAL production method execution path
        PaymentVerificationResponse actualResponse = paymentStrategy.verify(requestParams);

        // 3. ASSERT
        // Assert that the method returned a successful status payload
        assertNotNull(actualResponse);
        assertEquals(PaymentStatus.COMPLETE, actualResponse.getStatus());

        // Verify Business Side-Effects: Prove the database actually updated the records!
        Mockito.verify(paymentRepository, Mockito.times(1)).save(pendingPayment);
        Mockito.verify(appointmentRepository, Mockito.times(1)).save(appointment);

        // Prove that the state transitions happened correctly before saving
        assertEquals(PaymentStatus.COMPLETE, pendingPayment.getPaymentStatus());
        assertEquals(AppointmentStatus.CONFIRMED, appointment.getStatus());
    }
}
