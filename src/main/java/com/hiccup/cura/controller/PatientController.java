package com.hiccup.cura.controller;

import com.hiccup.cura.dto.reqeust.PatientRequestDto;
import com.hiccup.cura.dto.response.PatientResponseDto;
import com.hiccup.cura.security.CustomUser;
import com.hiccup.cura.service.PatientService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("${api.prefix}/patients")
@Tag(name="Patient", description = "Get patient details, update patient details")
public class PatientController {
    private final PatientService patientService;

    @GetMapping
    public ResponseEntity<PatientResponseDto> getPatient(@AuthenticationPrincipal UserDetails userDetails) {
        CustomUser customUser = (CustomUser) userDetails;
        return ResponseEntity.ok(patientService.getById(customUser.getId()));
    }

    @PatchMapping
    public ResponseEntity<PatientResponseDto> updatePatient(@AuthenticationPrincipal UserDetails userDetails, @RequestBody PatientRequestDto patientRequestDto) {
        CustomUser customUser = (CustomUser) userDetails;
        return ResponseEntity.ok(patientService.updateById(customUser.getId(), patientRequestDto));
    }
}
