package com.hiccup.cura.controller.admin;

import com.hiccup.cura.dto.response.ScheduleResponseDto;
import com.hiccup.cura.service.DoctorScheduleService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RequiredArgsConstructor
@RestController
@RequestMapping("${api.prefix}/admin/doctors/schedule")
public class AdminDoctorScheduleController {
    private final DoctorScheduleService scheduleService;
}
