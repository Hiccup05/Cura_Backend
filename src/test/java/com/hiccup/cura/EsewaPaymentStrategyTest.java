package com.hiccup.cura;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hiccup.cura.repository.PaymentRepository;
import com.hiccup.cura.service.payment.impl.EsewaPaymentStrategy;
import com.hiccup.cura.util.EsewaSignatureGenerator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.util.Base64;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
class EsewaPaymentStrategyTest {

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private EsewaSignatureGenerator signatureGenerator;

    @Spy // Use Spy for Jackson ObjectMapper to test real JSON parsing alongside mocks
    private ObjectMapper objectMapper = new ObjectMapper();

    @Spy
    private Clock clock = Clock.fixed(Instant.parse("2026-07-19T06:00:00Z"), ZoneId.of("Asia/Kathmandu"));

    @InjectMocks
    private EsewaPaymentStrategy esewaPaymentStrategy;

    @Test
    @DisplayName("Should throw SecurityException when computed signature does not match eSewa payload")
    void verify_SignatureMismatch_ThrowsSecurityException() throws Exception {
        String mockJsonResponse = "{\"transaction_code\":\"TXN123\",\"status\":\"COMPLETE\",\"total_amount\":\"1000\",\"transaction_uuid\":\"1\",\"product_code\":\"EPAYTEST\",\"signed_field_names\":\"transaction_code,status,total_amount,transaction_uuid,product_code,signed_field_names\",\"signature\":\"attacker_signature_here\"}";
        String base64Data = Base64.getEncoder().encodeToString(mockJsonResponse.getBytes());
        Map<String, String> requestParams = Map.of("data", base64Data);

        // Tell our mock signature generator to return what a real key WOULD generate
        Mockito.when(signatureGenerator.getSignature(Mockito.anyString())).thenReturn("expected_legitimate_signature");

        // 2. ACT & 3. ASSERT
        // Assert that the strategy class correctly spots the mismatch and throws a SecurityException
        assertThrows(SecurityException.class, () -> {
            esewaPaymentStrategy.verify(requestParams);
        });

        // Verify that execution was aborted instantly and NO database mutations occurred
        Mockito.verifyNoInteractions(paymentRepository);
    }

    @Test
    @DisplayName("Should throw IllegalStateException when crypto configuration or JVM environment fails")
    void verify_CryptoConfigError_ThrowsIllegalStateException() throws Exception {
        // 1. ARRANGE
        String mockJsonResponse = "{\"transaction_code\":\"TXN123\",\"status\":\"COMPLETE\",\"total_amount\":\"1000\",\"transaction_uuid\":\"1\",\"product_code\":\"EPAYTEST\",\"signed_field_names\":\"...\",\"signature\":\"...\"}";
        String base64Data = Base64.getEncoder().encodeToString(mockJsonResponse.getBytes());
        Map<String, String> requestParams = Map.of("data", base64Data);

        // Simulate an internal system crash inside the signature utility (e.g., severe JVM missing algorithm provider)
        Mockito.when(signatureGenerator.getSignature(Mockito.anyString()))
                .thenThrow(new IllegalStateException("Failed to initialize cryptographic signature component"));

        // 2. ACT & 3. ASSERT
        assertThrows(IllegalStateException.class, () -> {
            esewaPaymentStrategy.verify(requestParams);
        });
    }
}
