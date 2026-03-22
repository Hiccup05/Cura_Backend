package com.hiccup.cura.controller.admin;

import com.hiccup.cura.dto.response.PatientResponseDto;
import com.hiccup.cura.service.patient.PatientService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RequiredArgsConstructor
@RestController
@RequestMapping("${api.prefix}/admin/patients")
public class AdminPatientController {
    private final PatientService patientService;


}
