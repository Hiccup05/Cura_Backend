package com.hiccup.cura.controller.publics;

import com.hiccup.cura.dto.response.ScheduleResponseDto;
import com.hiccup.cura.service.DoctorScheduleService;
import com.hiccup.cura.service.doctor.DoctorService;
import com.hiccup.cura.service.doctor.specialization.SpecializationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("${api.prefix}/public/doctors")
@RequiredArgsConstructor
public class DoctorController {
    private final DoctorService doctorService;
    private final DoctorScheduleService doctorScheduleService;
    private final SpecializationService specializationService;
}
