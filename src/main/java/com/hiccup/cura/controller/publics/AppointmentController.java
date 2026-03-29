package com.hiccup.cura.controller.publics;

import com.hiccup.cura.service.AppointmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("${api.prefix}/appointment")
public class AppointmentController {
    private final AppointmentService appointmentService;
}
