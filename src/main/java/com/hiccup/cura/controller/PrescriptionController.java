package com.hiccup.cura.controller;

import com.hiccup.cura.dto.request.PrescriptionRequestDto;
import com.hiccup.cura.dto.response.PrescriptionResponseDto;
import com.hiccup.cura.security.CustomUser;
import com.hiccup.cura.service.PrescriptionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("${api.prefix}/appointments/prescriptions")
@Tag(name="Prescription", description = "Action in prescription by doctor")
public class PrescriptionController {
    private final PrescriptionService prescriptionService;

    @Operation(summary = "Fill in/update the prescription attached to an appointment.")
    @PatchMapping("/{id}")
    public ResponseEntity<PrescriptionResponseDto> updatePrescription(@Valid @RequestBody PrescriptionRequestDto prescriptionRequestDto, @PathVariable Long id, @AuthenticationPrincipal CustomUser user) {
        return ResponseEntity.ok(prescriptionService.updatePrescription(prescriptionRequestDto, id, user.getId()));
    }
}
