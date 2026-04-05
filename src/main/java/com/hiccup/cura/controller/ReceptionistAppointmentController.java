package com.hiccup.cura.controller;

import com.hiccup.cura.dto.reqeust.AppointmentRequestDto;
import com.hiccup.cura.dto.response.AppointmentResponseDto;
import com.hiccup.cura.dto.response.AppointmentSummaryDto;
import com.hiccup.cura.security.CustomUser;
import com.hiccup.cura.service.AppointmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("${api.prefix}/receptionist/appointment")
public class ReceptionistAppointmentController {
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

    @GetMapping("/my")
    public ResponseEntity<List<AppointmentSummaryDto>> getMyAppointment(@AuthenticationPrincipal CustomUser user){
        return ResponseEntity.ok(appointmentService.getMyAppointments(user.getId()));
    }

    @GetMapping()
    public ResponseEntity<List<AppointmentSummaryDto>> getAllReceptionistAppointments(@AuthenticationPrincipal CustomUser user){
        return ResponseEntity.ok(appointmentService.getAllReceptionistAppointments(user.getId()));
    }
}
