package com.hiccup.cura.controller;

import com.hiccup.cura.dto.reqeust.DoctorRequestDto;
import com.hiccup.cura.dto.response.AppointmentSummaryDto;
import com.hiccup.cura.dto.response.DoctorDto;
import com.hiccup.cura.dto.response.ScheduleResponseDto;
import com.hiccup.cura.security.CustomUser;
import com.hiccup.cura.service.AppointmentService;
import com.hiccup.cura.service.DoctorScheduleService;
import com.hiccup.cura.service.doctor.DoctorService;
import com.hiccup.cura.service.doctor.specialization.SpecializationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("${api.prefix}/doctors")
@RequiredArgsConstructor
public class DoctorController {
    private final DoctorService doctorService;
    private final DoctorScheduleService scheduleService;
    private final SpecializationService specializationService;
    private final AppointmentService appointmentService;

    @GetMapping
    public ResponseEntity<DoctorDto> getDoctorById(@AuthenticationPrincipal CustomUser customUser) {
        return ResponseEntity.ok(doctorService.getDoctor(customUser.getId()));
    }

    @PatchMapping
    public ResponseEntity<DoctorDto> updateDoctor(@AuthenticationPrincipal CustomUser customUser, @RequestBody DoctorRequestDto doctorDto) {
        return ResponseEntity.ok(doctorService.updateDoctor(customUser.getId(), doctorDto));
    }

    @GetMapping("/schedule/{scheduleId}")
    public ResponseEntity<ScheduleResponseDto> getDoctorSchedule(@AuthenticationPrincipal CustomUser customUser, @PathVariable Long scheduleId) {
        return ResponseEntity.ok(scheduleService.getScheduleOfDoctor(customUser.getId(), scheduleId));
    }

    @GetMapping("/appointments")
    public ResponseEntity<List<AppointmentSummaryDto>> getMyAppointment(@AuthenticationPrincipal CustomUser customUser) {
        return ResponseEntity.ok(appointmentService.getDoctorAppointments(customUser.getId()));
    }
}
