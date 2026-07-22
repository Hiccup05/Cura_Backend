package com.hiccup.cura.security;

import com.hiccup.cura.dto.request.LoginRequestDto;
import com.hiccup.cura.dto.response.LoginResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final AuthUtil authUtil;
    private final AuthenticationManager authenticationManager;

    public LoginResponseDto localLogin(LoginRequestDto request){
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()
                )
        );

        CustomUser customUser = (CustomUser) authentication.getPrincipal();
        String token = authUtil.generateTokenFromUser(customUser);
        return new LoginResponseDto(token, customUser.getId());
    }
}
