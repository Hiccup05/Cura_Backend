package com.hiccup.cura.controller.publics;

import com.hiccup.cura.dto.reqeust.AppointmentRequestDto;
import com.hiccup.cura.dto.response.AppointmentResponseDto;
import com.hiccup.cura.model.User;
import com.hiccup.cura.security.CustomUser;
import com.hiccup.cura.service.AppointmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequiredArgsConstructor
@RequestMapping("${api.prefix}/appointment")
public class AppointmentController {
    private final AppointmentService appointmentService;

    @PostMapping
    public ResponseEntity<AppointmentResponseDto> createAppointment(@RequestBody AppointmentRequestDto appointmentRequestDto, @AuthenticationPrincipal CustomUser user){
        AppointmentResponseDto created = appointmentService.createAppointment(appointmentRequestDto, user.getId());
        URI location= ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(created.getId())
                .toUri();
        return ResponseEntity.created(location).body(created);
    }

    @GetMapping("/{id}")
    public ResponseEntity<AppointmentResponseDto> getAppointment(@PathVariable Long id, @AuthenticationPrincipal User user){
        return null;
    }
}
