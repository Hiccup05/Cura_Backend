package com.hiccup.cura.service.doctor;

import com.hiccup.cura.dto.reqeust.DoctorRequestDto;
import com.hiccup.cura.dto.response.DoctorDto;
import com.hiccup.cura.repository.DoctorRepository;
import com.hiccup.cura.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DoctorService {
    private final DoctorRepository doctorRepository;
    private final UserRepository userRepository;

    public DoctorDto createDoctor(Long userId, DoctorRequestDto){

    }
}
