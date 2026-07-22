package com.hiccup.cura.controller.receptionist;

import com.hiccup.cura.dto.request.AppointmentRequestDto;
import com.hiccup.cura.dto.response.AppointmentResponseDto;
import com.hiccup.cura.dto.response.AppointmentSummaryDto;
import com.hiccup.cura.security.CustomUser;
import com.hiccup.cura.service.AppointmentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequiredArgsConstructor
@RequestMapping("${api.prefix}/receptionists/appointments")
@Tag(name = "Receptionist Appointments", description = "Book, Fetch")
public class ReceptionistAppointmentController {
    private final AppointmentService appointmentService;

    @Operation(summary = " Book a walk-in appointment (validated; status CONFIRMED, marked paid).")
    @PostMapping
    public ResponseEntity<AppointmentResponseDto> createAppointment(@Valid @RequestBody AppointmentRequestDto appointmentRequestDto, @AuthenticationPrincipal CustomUser user){
        AppointmentResponseDto created = appointmentService.createAppointment(appointmentRequestDto, user.getId());
        URI location= ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(created.getId())
                .toUri();
        return ResponseEntity.created(location).body(created);
    }

    @Operation(summary = "Get one receptionist-booked appointment.")
    @GetMapping("/{id}")
    public ResponseEntity<AppointmentResponseDto> getReceptionistAppointmentById(
            @PathVariable Long id
    ) {
        return ResponseEntity.ok(
                appointmentService.getReceptionistAppointmentById(id)
        );
    }

    @Operation(summary = "List receptionist-booked appointments, filterable by receptionist or walk-in name.")
    @GetMapping
    public ResponseEntity<Page<AppointmentSummaryDto>> getReceptionistAppointments(
            @RequestParam(required = false) Long receptionistId,
            @RequestParam(required = false) String walkInPatientName,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable= PageRequest.of(page, size);
        return ResponseEntity.ok(appointmentService.getReceptionistBookedAppointments(receptionistId, walkInPatientName, pageable));
    }
}
