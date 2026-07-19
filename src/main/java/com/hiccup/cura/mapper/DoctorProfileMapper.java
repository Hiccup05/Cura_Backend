package com.hiccup.cura.mapper;

import com.hiccup.cura.dto.response.DoctorDto;
import com.hiccup.cura.dto.response.PublicDoctorResponseDto;
import com.hiccup.cura.model.DoctorProfile;
import org.springframework.stereotype.Component;

@Component
public class DoctorProfileMapper {

    public DoctorDto toDto(DoctorProfile doctorProfile) {
        return DoctorDto.builder().doctorStatus(doctorProfile.getDoctorStatus())
                .id(doctorProfile.getId())
                .specialization(doctorProfile.getSpecialization())
                .licenseNumber(doctorProfile.getLicenseNumber())
                .yearsOfExperience(doctorProfile.getYearsOfExperience())
                .profilePictureUrl(doctorProfile.getUser().getProfilePictureUrl())
                .firstName(doctorProfile.getFirstName())
                .lastName(doctorProfile.getLastName())
                .build();
    }

    public PublicDoctorResponseDto toPublicDto(DoctorProfile doctor) {
        return PublicDoctorResponseDto.builder()
                .id(doctor.getId())
                .firstName(doctor.getFirstName())
                .lastName(doctor.getLastName())
                .specialization(doctor.getSpecialization())
                .yearsOfExperience(doctor.getYearsOfExperience())
                .licenseNumber(doctor.getLicenseNumber())
                .doctorStatus(doctor.getDoctorStatus())
                .profilePictureUrl(doctor.getUser().getProfilePictureUrl())
                .build();
    }
}
