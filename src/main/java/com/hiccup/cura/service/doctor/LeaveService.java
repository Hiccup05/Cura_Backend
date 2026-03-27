package com.hiccup.cura.service.doctor;

import com.hiccup.cura.repository.DoctorLeaveRepository;
import com.hiccup.cura.repository.DoctorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LeaveService {
    private final DoctorLeaveRepository doctorLeaveRepository;
    private final DoctorRepository doctorRepository;

    
}
