package com.hiccup.cura.controller;

import com.hiccup.cura.dto.request.AppointmentRequestDto;
import com.hiccup.cura.dto.response.AppointmentResponseDto;
import com.hiccup.cura.dto.response.AppointmentSummaryDto;
import com.hiccup.cura.exception.ErrorResponse;
import com.hiccup.cura.security.CustomUser;
import com.hiccup.cura.service.AppointmentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
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
@RequestMapping("${api.prefix}/appointments")
@Tag(name="Appointments", description = "Booking, cancellation, Retrieve")
public class AppointmentController {
    private final AppointmentService appointmentService;

    @Operation(summary = " Book an appointment with a doctor for a medical service (validated; status forced to PENDING, payment ONLINE).")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Appointment booked as PENDING; Location header points at the created appointment."),
            @ApiResponse(responseCode = "400", description = "Missing required fields; slot already booked; requested time outside the doctor's schedule or in the past; doctor lacks the service's specialization; doctor on leave; slot capacity full; or patient profile incomplete.",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Doctor, medical service, or schedule for that weekday not found.",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
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

    @Operation(summary = "List my appointments, paginated and sortable.")
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

    @Operation(summary = "Get one of my appointments in full detail.")
    @GetMapping("/{id}")
    public ResponseEntity<AppointmentResponseDto> getAppointment(@PathVariable Long id, @AuthenticationPrincipal CustomUser user){
        return ResponseEntity.ok(appointmentService.getAppointment(user.getId(), id));
    }

    @Operation(summary = " Cancel my appointment (subject to the 24-hour cancellation rules).")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Appointment cancelled; returns the updated appointment."),
            @ApiResponse(responseCode = "400", description = "Already cancelled, or outside the allowed cancellation window (24-hour rules).",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "No appointment with this id belongs to the authenticated user.",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PatchMapping("/{id}/cancel")
    public ResponseEntity<AppointmentResponseDto> cancelAppointment(@PathVariable Long id, @AuthenticationPrincipal CustomUser user){
        return ResponseEntity.ok(appointmentService.cancelAppointment(user.getId(), id));
    }
}
