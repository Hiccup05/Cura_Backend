package com.hiccup.cura.controller;

import com.hiccup.cura.dto.reqeust.DoctorRequestDto;
import com.hiccup.cura.dto.response.AppointmentResponseDto;
import com.hiccup.cura.dto.response.AppointmentSummaryDto;
import com.hiccup.cura.dto.response.DoctorDto;
import com.hiccup.cura.dto.response.ScheduleResponseDto;
import com.hiccup.cura.enums.AppointmentStatus;
import com.hiccup.cura.security.CustomUser;
import com.hiccup.cura.service.AppointmentService;
import com.hiccup.cura.service.doctor.DoctorScheduleService;
import com.hiccup.cura.service.doctor.DoctorService;
import com.hiccup.cura.service.SpecializationService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("${api.prefix}/doctors")
@RequiredArgsConstructor
@Tag(name = "Doctors", description = "Fetch, Update, Schedules, Appointments")
public class DoctorController {
    private final DoctorService doctorService;
    private final DoctorScheduleService scheduleService;
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

    @GetMapping("/schedule")
    public ResponseEntity<List<ScheduleResponseDto>> getDoctorSchedules(@AuthenticationPrincipal CustomUser customUser) {
        return ResponseEntity.ok(scheduleService.getSchedulesOfDoctor(customUser.getId()));
    }

    @GetMapping("appointments")
    public ResponseEntity<Page<AppointmentSummaryDto>> getDoctorAppointments(
            @AuthenticationPrincipal CustomUser user,
            @RequestParam(required = false) String patientName,
            @RequestParam(required = false) String walkInPatientName,
            @RequestParam(required = false) String receptionistName,
            @RequestParam(required = false) AppointmentStatus status,
            @RequestParam(required = false) String dateFrom,
            @RequestParam(required = false) String dateTo,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "appointmentDate") String sortBy,
            @RequestParam(defaultValue = "DESC") String sortDir
    ) {
        Sort sort = Sort.by(Sort.Direction.fromString(sortDir), sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<AppointmentSummaryDto> result = appointmentService.getDoctorAppointmentsFiltered(
                user.getId(),
                patientName,
                walkInPatientName,
                receptionistName,
                status,
                dateFrom,
                dateTo,
                pageable
        );

        return ResponseEntity.ok(result);
    }

    @GetMapping("appointment/{id}")
    public ResponseEntity<AppointmentResponseDto> getAppointment(@PathVariable Long id, @AuthenticationPrincipal CustomUser user){
        return ResponseEntity.ok(appointmentService.getDoctorAppointment(user.getId(), id));
    }


}
