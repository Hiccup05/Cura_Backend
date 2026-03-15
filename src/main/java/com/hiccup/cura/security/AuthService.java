package com.hiccup.cura.security;

import com.hiccup.cura.dto.reqeust.LoginRequestDto;
import com.hiccup.cura.dto.response.LoginResponseDto;
import com.hiccup.cura.dto.reqeust.SignUpRequestDto;
import com.hiccup.cura.dto.response.SignupResponseDto;
import com.hiccup.cura.enums.AuthType;
import com.hiccup.cura.enums.RoleType;
import com.hiccup.cura.model.User;
import com.hiccup.cura.repository.RoleRepository;
import com.hiccup.cura.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Set;

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
