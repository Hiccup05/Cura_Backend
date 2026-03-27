package com.hiccup.cura.controller;

import com.hiccup.cura.dto.reqeust.DoctorRequestDto;
import com.hiccup.cura.dto.response.DoctorDto;
import com.hiccup.cura.dto.response.ScheduleResponseDto;
import com.hiccup.cura.security.CustomUser;
import com.hiccup.cura.service.DoctorScheduleService;
import com.hiccup.cura.service.doctor.DoctorService;
import com.hiccup.cura.service.doctor.specialization.SpecializationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("${api.prefix}/doctors")
@RequiredArgsConstructor
public class DoctorController {
    private final DoctorService doctorService;
    private final DoctorScheduleService doctorScheduleService;
    private final SpecializationService specializationService;

    @GetMapping
    public ResponseEntity<DoctorDto> getDoctorById(@AuthenticationPrincipal CustomUser customUser) {
        return ResponseEntity.ok(doctorService.getDoctor(customUser.getId()));
    }

}
