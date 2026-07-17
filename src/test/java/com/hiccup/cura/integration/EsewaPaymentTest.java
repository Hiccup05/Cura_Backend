package com.hiccup.cura.integration;


import com.github.tomakehurst.wiremock.junit5.WireMockExtension;
import com.hiccup.cura.enums.PaymentProvider;
import com.hiccup.cura.enums.PaymentStatus;
import com.hiccup.cura.enums.RoleType;
import com.hiccup.cura.model.Appointment;
import com.hiccup.cura.model.Payment;
import com.hiccup.cura.model.Role;
import com.hiccup.cura.model.User;
import com.hiccup.cura.repository.*;
import com.hiccup.cura.security.CustomUser;
import com.hiccup.cura.util.TestDataFactory;
import org.junit.jupiter.api.*;
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
    private AppointmentRepository appointmentRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MedicalServiceRepository medicalServiceRepository;

    @Autowired
    private PatientRepository patientRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private TestDataFactory testDataFactory;

    @DynamicPropertySource
    static void overrideKhaltiUrl(DynamicPropertyRegistry registry) {
        registry.add("khalti.base-url", wireMock::baseUrl);
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
}
