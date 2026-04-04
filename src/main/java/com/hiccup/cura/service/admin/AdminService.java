package com.hiccup.cura.service.admin;

import com.hiccup.cura.dto.response.AdminProfileDto;
import com.hiccup.cura.dto.response.DoctorDto;
import com.hiccup.cura.exception.custom.ResourceNotFoundException;
import com.hiccup.cura.model.DoctorProfile;
import com.hiccup.cura.model.User;
import com.hiccup.cura.repository.AppointmentRepository;
import com.hiccup.cura.repository.DoctorRepository;
import com.hiccup.cura.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminService {
    private final UserRepository userRepository;
    private final DoctorRepository doctorRepository;
    private final AppointmentRepository appointmentRepository;

    public AdminProfileDto getAdminProfile(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Admin not found with id " + userId));
        return AdminProfileDto.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .build();
    }

    public List<DoctorDto> getAllDoctors() {
        return doctorRepository.findAll().stream()
                .map(this::mapToDoctorDto).toList();
    }


    private DoctorDto mapToDoctorDto(DoctorProfile doctor) {
        return DoctorDto.builder()
                .id(doctor.getId())
                .licenseNumber(doctor.getLicenseNumber())
                .yearsOfExperience(doctor.getYearsOfExperience())
                .specialization(doctor.getSpecialization())
                .doctorStatus(doctor.getDoctorStatus())
                .build();
    }
}
