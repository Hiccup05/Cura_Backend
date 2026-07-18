package com.hiccup.cura.integration;


import com.github.tomakehurst.wiremock.junit5.WireMockExtension;
import com.hiccup.cura.enums.PaymentProvider;
import com.hiccup.cura.enums.PaymentStatus;
import com.hiccup.cura.enums.PaymentType;
import com.hiccup.cura.enums.RoleType;
import com.hiccup.cura.model.Appointment;
import com.hiccup.cura.model.Payment;
import com.hiccup.cura.model.Role;
import com.hiccup.cura.model.User;
import com.hiccup.cura.repository.*;
import com.hiccup.cura.security.CustomUser;
import com.hiccup.cura.util.EsewaSignatureGenerator;
import com.hiccup.cura.util.TestDataFactory;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.OffsetDateTime;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@Testcontainers
@ActiveProfiles("test")
public class EsewaPaymentTest {
    @Container
    @ServiceConnection(name = "postgres")
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16");

    @RegisterExtension
    static WireMockExtension wireMock = WireMockExtension.newInstance()
            .options(wireMockConfig().dynamicPort())
            .build();
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;


    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private AppointmentRepository appointmentRepository;

    @Autowired
    private TestDataFactory testDataFactory;

    @Autowired
    private EsewaSignatureGenerator signatureGenerator;

    @BeforeEach
    void cleanDatabase() {
        // Deleting payments directly is silently undone by the
        // Appointment.payment cascade (the managed Appointment re-saves the
        // payment on flush), so remove the owning appointments instead —
        // CascadeType.ALL propagates the delete to their payments.
        appointmentRepository.deleteAll();
        paymentRepository.deleteAll();
    }

    @Test
    void testInitiate() throws Exception {
        Appointment appointment = testDataFactory.createPendingAppointmentForNewPatient(BigDecimal.ONE);
        User user=userRepository.findById(appointment.getPatient().getId()).get();
        CustomUser mockUser=new CustomUser(user);
        assertFalse(user.getRole().contains(Role.builder().name(RoleType.DOCTOR).build()));
        assertFalse(user.getRole().contains(Role.builder().name(RoleType.ADMIN).build()));


        mockMvc.perform(post("/api/v1/payments/initiate")
                        .param("appointmentId", appointment.getId().toString())
                        .param("provider", PaymentProvider.ESEWA.toString())
                        .with(user(mockUser)))
                .andExpect(status().isOk());

        Payment byPidx = paymentRepository.findByPidx(String.valueOf(appointment.getId()));
        assertNotNull(byPidx);
        assertEquals(PaymentStatus.PENDING, byPidx.getPaymentStatus());
    }

     // same bean your service uses

    @Test
    void testVerify() throws Exception {
        String transactionCode = "000AWEO";
        String status = "COMPLETE";
        String totalAmount = "1000.0";
        String transactionUuid = "1";
        String productCode = "EPAYTEST";
        String signedFieldNames = "transaction_code,status,total_amount,transaction_uuid,product_code";

        String signatureMessage = "transaction_code=" + transactionCode +
                ",status=" + status +
                ",total_amount=" + totalAmount +
                ",transaction_uuid=" + transactionUuid +
                ",product_code=" + productCode +
                ",signed_field_names=" + signedFieldNames;

        String signature = signatureGenerator.getSignature(signatureMessage);

        String json = """
        {
            "transaction_code": "%s",
            "status": "%s",
            "total_amount": "%s",
            "transaction_uuid": "%s",
            "product_code": "%s",
            "signed_field_names": "%s",
            "signature": "%s"
        }
        """.formatted(transactionCode, status, totalAmount, transactionUuid, productCode, signedFieldNames, signature);

        String base64Data = Base64.getEncoder().encodeToString(json.getBytes(StandardCharsets.UTF_8));

        testDataFactory.createPendingPayment(BigDecimal.ONE);

        mockMvc.perform(post("/api/v1/payments/verify/{provider}", PaymentProvider.ESEWA)
                        .param("data", base64Data))
                .andExpect(status().isOk());

        Payment byPidx = paymentRepository.findByPidx(String.valueOf(1));
        assertNotNull(byPidx);
        assertEquals(PaymentStatus.COMPLETE, byPidx.getPaymentStatus());
    }
}
