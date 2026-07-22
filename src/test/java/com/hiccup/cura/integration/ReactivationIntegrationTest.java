package com.hiccup.cura.integration;

import com.hiccup.cura.config.ClockConfig;
import com.hiccup.cura.dto.request.ReactivationTokenRequestDto;
import com.hiccup.cura.model.ReactivationToken;
import com.hiccup.cura.model.User;
import com.hiccup.cura.repository.ReactivationTokenRepository;
import com.hiccup.cura.repository.UserRepository;
import jakarta.mail.Session;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.postgresql.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import tools.jackson.databind.ObjectMapper;

import java.time.Clock;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@Testcontainers
@ActiveProfiles("test")
class ReactivationIntegrationTest {

    static final Clock FIXED_CLOCK =
            Clock.fixed(Instant.parse("2026-07-19T06:00:00Z"), ClockConfig.CLINIC_ZONE);

    @TestConfiguration
    static class FixedClockConfig {
        @Bean
        @Primary
        Clock fixedClock() {
            return FIXED_CLOCK;
        }
    }

    @Container
    @ServiceConnection(name = "postgres")
    static PostgreSQLContainer postgres=new PostgreSQLContainer("postgres:16");

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    MockMvc mockMvc;

    @Autowired
    UserRepository userRepository;

    @Autowired
    ReactivationTokenRepository reactivationTokenRepository;

    @MockitoBean
    JavaMailSender javaMailSender;

    @BeforeEach
    void cleanDatabase() {
        reactivationTokenRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    void testReactivationTokenInitiate() throws Exception {
        MimeMessage dummyMessage = new MimeMessage((Session) null);
        Mockito.when(javaMailSender.createMimeMessage()).thenReturn(dummyMessage);
        User user= User.builder()
                        .email("bein054717@gmail.com")
                                .active(false).build();
        userRepository.save(user);

        mockMvc.perform(post("/api/v1/reactivate/initiate")
                        .param("email", "bein054717@gmail.com"))
                .andExpect(status().isOk());
        List<ReactivationToken> all = reactivationTokenRepository.findAll();
        ReactivationToken token=all.getFirst();
        assertEquals("bein054717@gmail.com",token.getEmail());
        assertFalse(token.isUsed());
        assertNotNull(token.getExpiresAt());
        verify(javaMailSender).send(any(MimeMessage.class));
    }

    @Test
    void testReactivationTokenVerify() throws Exception {
        MimeMessage dummyMessage = new MimeMessage((Session) null);
        Mockito.when(javaMailSender.createMimeMessage()).thenReturn(dummyMessage);
        User user= User.builder()
                .email("bein054717@gmail.com")
                .active(false).build();
        userRepository.save(user);

        String token=UUID.randomUUID().toString();

        reactivationTokenRepository.save(ReactivationToken.builder()
                .token(token)
                .createdAt(FIXED_CLOCK.instant())
                .expiresAt(FIXED_CLOCK.instant().plus(24, ChronoUnit.HOURS))
                .email("bein054717@gmail.com")
                .used(false)
                .build());
        ReactivationTokenRequestDto tokenRequestDto=new ReactivationTokenRequestDto("bein054717@gmail.com", token);

        mockMvc.perform(post("/api/v1/reactivate/verify")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(tokenRequestDto)))
                .andExpect(status().isNoContent());
        user=userRepository.findByEmail(tokenRequestDto.getEmail()).get();
        List<ReactivationToken> all = reactivationTokenRepository.findAll();
        ReactivationToken token1=all.getFirst();
        assertTrue(user.isActive());
        assertTrue(token1.isUsed());
        verify(javaMailSender).send(any(MimeMessage.class));
    }

    @Test
    void testReactivationTokenVerify_TokenUsed() throws Exception {
        User user= User.builder()
                .email("bein054717@gmail.com")
                .active(false).build();
        userRepository.save(user);

        String token=UUID.randomUUID().toString();

        reactivationTokenRepository.save(ReactivationToken.builder()
                .token(token)
                .createdAt(FIXED_CLOCK.instant())
                .expiresAt(FIXED_CLOCK.instant().plus(24, ChronoUnit.HOURS))
                .email("bein054717@gmail.com")
                .used(true)
                .build());
        ReactivationTokenRequestDto tokenRequestDto=new ReactivationTokenRequestDto("bein054717@gmail.com", token);

        mockMvc.perform(post("/api/v1/reactivate/verify")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(tokenRequestDto)))
                .andExpect(status().isBadRequest());
        User unchangedUser=userRepository.findByEmail("bein054717@gmail.com").orElseThrow();
        ReactivationToken token1=reactivationTokenRepository.findAll().getFirst();
        assertFalse(unchangedUser.isActive());
        assertTrue(token1.isUsed());
        verifyNoInteractions(javaMailSender);

        //no further testing needed as it will return from service method earlier before doing other testing so no need the email service also
    }

    @Test
    void testReactivationTokenVerify_TokenExpired() throws Exception {
        User user= User.builder()
                .email("bein054717@gmail.com")
                .active(false).build();
        userRepository.save(user);

        String token=UUID.randomUUID().toString();

        reactivationTokenRepository.save(ReactivationToken.builder()
                .token(token)
                .createdAt(FIXED_CLOCK.instant().minus(26, ChronoUnit.HOURS))
                .expiresAt(FIXED_CLOCK.instant().minus(2, ChronoUnit.HOURS))
                .email("bein054717@gmail.com")
                .used(false)
                .build());
        ReactivationTokenRequestDto tokenRequestDto=new ReactivationTokenRequestDto("bein054717@gmail.com", token);

        mockMvc.perform(post("/api/v1/reactivate/verify")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(tokenRequestDto)))
                .andExpect(status().isGone());
        User unchangedUser=userRepository.findByEmail("bein054717@gmail.com").orElseThrow();
        ReactivationToken token1=reactivationTokenRepository.findAll().getFirst();
        assertFalse(unchangedUser.isActive());
        assertFalse(token1.isUsed());
        verifyNoInteractions(javaMailSender);

        //no further testing needed as it will return from service method earlier before doing other testing so no need the email service also
    }
}
