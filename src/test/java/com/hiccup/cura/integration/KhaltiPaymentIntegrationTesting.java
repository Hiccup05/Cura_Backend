package com.hiccup.cura.integration;

import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.junit5.WireMockExtension;
import com.hiccup.cura.enums.AppointmentStatus;
import com.hiccup.cura.enums.PaymentProvider;
import com.hiccup.cura.enums.RoleType;
import com.hiccup.cura.model.*;
import com.hiccup.cura.repository.*;
import com.hiccup.cura.security.CustomUser;
import com.hiccup.cura.util.TestDataFactory;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.math.BigDecimal;
import java.util.Set;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@Testcontainers
@ActiveProfiles("test")
public class KhaltiPaymentIntegrationTesting {
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
    private AppointmentRepository appointmentRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MedicalServiceRepository medicalServiceRepository;

    @Autowired
    private PatientRepository patientRepository;

    @Autowired
    private RoleRepository roleRepository;

    @DynamicPropertySource
    static void overrideKhaltiUrl(DynamicPropertyRegistry registry) {
        registry.add("khalti.base-url", wireMock::baseUrl);
    }

    @Test
    void testPaymentInitiate() throws Exception {
        Role role=new Role(1L, RoleType.PATIENT);
        role=roleRepository.save(role);
        Long appointmentId=1L;
        User user= TestDataFactory.createPatient();
        user.setRole(Set.of(role));
        user=userRepository.save(user);
        MedicalService medicalService=new MedicalService();
        medicalService.setPrice(BigDecimal.ONE);
        medicalService.setName(" ");
        medicalServiceRepository.save(medicalService);
        PatientProfile profile=TestDataFactory.createPatientProfile();
        profile.setUser(user);
        patientRepository.save(profile);
        Appointment appointment=new Appointment();
        appointment.setPatient(profile);
        appointment.setStatus(AppointmentStatus.PENDING);
        appointment.setMedicalService(medicalService);
        appointmentRepository.save(appointment);

        CustomUser mockUser= new CustomUser(user);

        wireMock.stubFor(
                WireMock.post(urlEqualTo("/epayment/initiate/"))
                        .willReturn(
                                aResponse()
                                        .withStatus(200)
                                        .withHeader("Content-Type", "application/json")
                                        .withBody("""
                    {
                        "pidx": "fake-pidx-123",
                        "payment_url": "https://fake-khalti.test/pay/fake-pidx-123",
                        "expires_at": "2026-07-20T10:00:00+05:45",
                        "expires_in": 600
                    }
                    """)
                        )
        );

        mockMvc.perform(post("/api/v1/payments/initiate")
                        .param("appointmentId", appointmentId.toString())
                        .param("provider", PaymentProvider.KHALTI.toString())
                        .with(user(mockUser)))
                .andExpect(status().isOk());
    }
}
