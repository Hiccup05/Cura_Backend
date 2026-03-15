package com.hiccup.cura.controller;

import com.hiccup.cura.dto.reqeust.LoginRequestDto;
import com.hiccup.cura.dto.response.LoginResponseDto;
import com.hiccup.cura.security.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("${api.prefix}/auth")
public class AuthController {
    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDto> localLogin(@RequestBody LoginRequestDto requestDto){
        return ResponseEntity.accepted().body(authService.localLogin(requestDto));
    }
}
