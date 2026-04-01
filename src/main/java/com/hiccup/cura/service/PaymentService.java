package com.hiccup.cura.service;

import com.hiccup.cura.repository.AppointmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class PaymentService {
    private final AppointmentRepository appointmentRepository;

}
