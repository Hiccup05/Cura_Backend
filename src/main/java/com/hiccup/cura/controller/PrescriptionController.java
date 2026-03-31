package com.hiccup.cura.controller;

import com.hiccup.cura.dto.reqeust.PrescriptionRequestDto;
import com.hiccup.cura.dto.response.PrescriptionResponseDto;
import com.hiccup.cura.security.CustomUser;
import com.hiccup.cura.service.PrescriptionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("${api.prefix}/appointment/prescription")
public class PrescriptionController {
    private final PrescriptionService prescriptionService;

    @PatchMapping("/{id}")
    public ResponseEntity<PrescriptionResponseDto> updatePrescription(@Valid @RequestBody PrescriptionRequestDto prescriptionRequestDto, @PathVariable Long id, @AuthenticationPrincipal CustomUser user) {
        return ResponseEntity.ok(prescriptionService.updatePrescription(prescriptionRequestDto, id, user.getId()));
    }
}
