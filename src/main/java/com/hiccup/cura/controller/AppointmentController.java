package com.hiccup.cura.controller;

import com.hiccup.cura.dto.reqeust.AppointmentRequestDto;
import com.hiccup.cura.dto.response.AppointmentResponseDto;
import com.hiccup.cura.dto.response.AppointmentSummaryDto;
import com.hiccup.cura.security.CustomUser;
import com.hiccup.cura.service.AppointmentService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequiredArgsConstructor
@RequestMapping("${api.prefix}/appointment")
@Tag(name="Appointments", description = "Booking, cancellation, Retrieve")
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

    @GetMapping
    public ResponseEntity<Page<AppointmentSummaryDto>> getMyAppointments(@AuthenticationPrincipal CustomUser user,
                                                                         @RequestParam(defaultValue = "0") int page,
                                                                         @RequestParam(defaultValue = "10") int size,
                                                                         @RequestParam(defaultValue="DESC") String sortDir,
                                                                         @RequestParam(defaultValue = "appointmentDate") String sortBy){
        Sort sort=Sort.by(Sort.Direction.fromString(sortDir), sortBy);
        Pageable pageable= PageRequest.of(page, size, sort);
        return ResponseEntity.ok(appointmentService.getMyAppointments(user.getId(), pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<AppointmentResponseDto> getAppointment(@PathVariable Long id, @AuthenticationPrincipal CustomUser user){
        return ResponseEntity.ok(appointmentService.getAppointment(user.getId(), id));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<AppointmentResponseDto> cancelAppointment(@PathVariable Long id, @AuthenticationPrincipal CustomUser user){
        return ResponseEntity.ok(appointmentService.cancelAppointment(user.getId(), id));
    }
}
