package com.hiccup.cura.security.oauth2;

import com.hiccup.cura.dto.response.LoginResponseDto;
import com.hiccup.cura.security.AuthService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;

@Component
@RequiredArgsConstructor
@Slf4j
public class Oauth2SuccessHandler implements AuthenticationSuccessHandler {

    private final AuthService authService;
    private final ObjectMapper objectMapper;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        OAuth2AuthenticationToken token=(OAuth2AuthenticationToken) authentication;
        OAuth2User oAuth2User=(OAuth2User) authentication.getPrincipal();
        log.info(oAuth2User.getName());
        log.info(oAuth2User.toString());

        String registrationId =token.getAuthorizedClientRegistrationId();

        ResponseEntity<LoginResponseDto> loginResponseDtoResponseEntity =
                authService.handleOauth2LoginRequest(oAuth2User, registrationId);

        response.setStatus(loginResponseDtoResponseEntity.getStatusCode().value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.getWriter().write(objectMapper.writeValueAsString(loginResponseDtoResponseEntity.getBody()));

    }
}
