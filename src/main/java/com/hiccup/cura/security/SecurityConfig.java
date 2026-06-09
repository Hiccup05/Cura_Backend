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
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import tools.jackson.databind.ObjectMapper;

import java.util.List;

@EnableWebSecurity
@RequiredArgsConstructor
@Configuration
@Slf4j
public class SecurityConfig {

    private final JwtFilter jwtFilter;
    private final Oauth2SuccessHandler oauth2SuccessHandler;
    private final ObjectMapper mapper;
    private static final String G_URL="${api.prefix}/auth/**";
    private static final String A_URL="${api.prefix}/admin/**";
    private static final String D_URL="${api.prefix}/doctor/**";
    private static final String P_URL="${api.prefix}/public/**";
    private static final String PA_URL="${api.prefix}/patients/**";
    private static final String APPOINTMENT_URL= "${api.prefix}/appointment/**";
    private static final String PRESCRIPTION_URL = "${api.prefix}/appointment/prescription/**";
    private static final String RECEPTIONIST_URL="${api.prefix}/receptionist/**";

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http){
        return http
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(s->s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .cors(c-> c.configurationSource(corsConfigurationSource()))
                .authorizeHttpRequests(auth->
                        auth.requestMatchers(G_URL).permitAll()
                                .requestMatchers("${api.prefix}/payment/verify").permitAll()
                                .requestMatchers(P_URL).permitAll()
                                .requestMatchers(A_URL).hasRole(RoleType.ADMIN.name())
                                .requestMatchers(D_URL).hasRole(RoleType.DOCTOR.name())
                                .requestMatchers(PRESCRIPTION_URL).hasAnyRole(RoleType.DOCTOR.name())
                                .requestMatchers(PA_URL).hasRole(RoleType.PATIENT.name())
                                .requestMatchers(APPOINTMENT_URL).hasAnyRole(RoleType.PATIENT.name(), RoleType.RECEPTIONIST.name())
                                .requestMatchers(RECEPTIONIST_URL).hasAnyRole(RoleType.RECEPTIONIST.name())
                                .anyRequest().authenticated()
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

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        configuration.setAllowedOrigins(List.of("http://localhost:3000"));
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
