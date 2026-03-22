package com.hiccup.cura.controller;

import com.hiccup.cura.dto.response.PatientResponseDto;
import com.hiccup.cura.security.CustomUser;
import com.hiccup.cura.service.patient.PatientService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("${api.prefix}/patients")
public class PatientController {
    private final PatientService patientService;

}
