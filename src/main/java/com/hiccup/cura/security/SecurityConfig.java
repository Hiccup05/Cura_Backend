package com.hiccup.cura.security;

import com.hiccup.cura.enums.RoleType;
import com.hiccup.cura.security.jwt.JwtFilter;
import com.hiccup.cura.security.oauth2.Oauth2SuccessHandler;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import tools.jackson.databind.ObjectMapper;

import java.util.Set;

@EnableWebSecurity
@RequiredArgsConstructor
@Configuration
@Slf4j
public class SecurityConfig {

    private final JwtFilter jwtFilter;
    private final Oauth2SuccessHandler oauth2SuccessHandler;
    private final ObjectMapper mapper;
    private static final String G_URL="/api/v1/auth/**";
    private static final String A_URL="/api/v1/admin/**";
    private static final String D_URL="/api/v1/doctor/**";
    private static final String P_URL="/api/v1/public/**";
    private static final String PA_URL="/api/v1/patients/**";
    private static final String APPOINTMENT_URL= "/api/v1/appointment/**";

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http){
        return http
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(s->s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth->
                        auth.requestMatchers(G_URL).permitAll()
                                .requestMatchers(P_URL).permitAll()
                                .requestMatchers(A_URL).hasRole(RoleType.ADMIN.name())
                                .requestMatchers(D_URL).hasRole(RoleType.DOCTOR.name())
                                .requestMatchers(PA_URL).hasRole(RoleType.PATIENT.name())
                                .requestMatchers(APPOINTMENT_URL).hasAnyRole(RoleType.PATIENT.name(), RoleType.RECEPTIONIST.name())
                                .anyRequest().permitAll()
                )
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
                .oauth2Login(oAuth2-> oAuth2.failureHandler(
                        (HttpServletRequest request, HttpServletResponse response, AuthenticationException exception)-> log.error("Oauth2Error: {}", exception.getMessage())
                )
                                .successHandler(oauth2SuccessHandler)
                )
                .exceptionHandling(e->
                        e.accessDeniedHandler((HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException)->{
                            log.error("Access Denied exception: ", accessDeniedException);
                            response.setStatus(HttpStatus.FORBIDDEN.value());
                            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                            response.getWriter().write(mapper.writeValueAsString("The URI trying to access is not accessible."));
                        })
                                .authenticationEntryPoint((request, response, ex)->{
                                    log.error("Authentication error: ", ex);
                                    response.setStatus(HttpStatus.UNAUTHORIZED.value());
                                    response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                                    response.getWriter().write(mapper.writeValueAsString("Opps! Token is invalid. Login Again"));
                                })
                )
                .build();
    }
}
