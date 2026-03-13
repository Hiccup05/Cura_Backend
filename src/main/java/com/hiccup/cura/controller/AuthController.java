package com.hiccup.cura.controller;

import com.hiccup.cura.security.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("${api.prefix}/auth/")
public class AuthController {
    private final AuthService authService;
}
