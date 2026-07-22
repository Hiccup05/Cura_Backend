package com.hiccup.cura.controller;

import com.hiccup.cura.dto.request.LoginRequestDto;
import com.hiccup.cura.dto.response.LoginResponseDto;
import com.hiccup.cura.security.AuthService;
import com.hiccup.cura.security.CustomUser;
import com.hiccup.cura.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RequiredArgsConstructor
@RestController
@RequestMapping("${api.prefix}/auth")
@Tag(name="Authentication", description = "Admin login, Get profile, Logout")
public class AuthController {
    private final AuthService authService;
    private final UserService userService;

    @Operation(summary = "Authenticate with username/password, returns JWT and sets an HttpOnly cookie.")
    @PostMapping("/login")
    public ResponseEntity<LoginResponseDto> localLogin(@Valid @RequestBody LoginRequestDto requestDto, HttpServletResponse response) {
        LoginResponseDto loginResponseDto = authService.localLogin(requestDto);
        Cookie cookie=new Cookie("token", loginResponseDto.getJwtToken());
        cookie.setMaxAge(60*60);
        cookie.setPath("/");
        cookie.setHttpOnly(true);
        response.addCookie(cookie);
        return ResponseEntity.ok().body(loginResponseDto);
    }

    @Operation(summary = "Return the logged-in user's roles and profile picture URL.")
    @GetMapping("/me")
    public ResponseEntity<Map<String, Object>> me(@AuthenticationPrincipal CustomUser user) {
        Map<String, Object> role = Map.of("role", user.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList(),
                "profilePictureUrl", userService.getProfilePictureUrl(user.getId())
        );
        return ResponseEntity.ok(role);
    }

    @Operation(summary="Clear the JWT cookie.")
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletResponse response){
        Cookie cookie=new Cookie("token", null);
        cookie.setMaxAge(0);
        cookie.setPath("/");
        cookie.setHttpOnly(true);
        response.addCookie(cookie);
        return  ResponseEntity.noContent().build();
    }
}
