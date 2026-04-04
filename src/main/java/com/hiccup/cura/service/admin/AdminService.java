package com.hiccup.cura.service.admin;

import com.hiccup.cura.repository.AppointmentRepository;
import com.hiccup.cura.repository.DoctorRepository;
import com.hiccup.cura.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AdminService {
    private final UserRepository userRepository;
    private final DoctorRepository doctorRepository;
    private final AppointmentRepository appointmentRepository;


}
