package com.hiccup.cura.controller;

import com.hiccup.cura.dto.reqeust.LoginRequestDto;
import com.hiccup.cura.dto.response.LoginResponseDto;
import com.hiccup.cura.security.AuthService;
import com.hiccup.cura.security.CustomUser;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.Nullable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@RestController
@RequestMapping("${api.prefix}/auth")
public class AuthController {
    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDto> localLogin(@RequestBody LoginRequestDto requestDto, HttpServletResponse response) {
        LoginResponseDto loginResponseDto = authService.localLogin(requestDto);
        Cookie cookie=new Cookie("token", loginResponseDto.getJwtToken());
        cookie.setMaxAge(60*60);
        cookie.setPath("/");
        cookie.setHttpOnly(true);
        response.addCookie(cookie);
        return ResponseEntity.accepted().body(loginResponseDto);
    }

    @GetMapping("/me")
    public ResponseEntity<Map<String, List<String>>> me(@AuthenticationPrincipal CustomUser user) {
        Map<String, List<String>> role = Map.of("role", user.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList());
        return ResponseEntity.ok(role);
    }
}
