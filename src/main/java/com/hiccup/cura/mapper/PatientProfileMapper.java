package com.hiccup.cura.mapper;

import com.hiccup.cura.dto.response.PatientResponseDto;
import com.hiccup.cura.model.PatientProfile;
import org.springframework.stereotype.Component;

@Component
public class PatientProfileMapper {

    public PatientResponseDto toDto(PatientProfile profile) {
        return PatientResponseDto.builder()
                .id(profile.getId())
                .firstName(profile.getFirstName())
                .lastName(profile.getLastName())
                .dateOfBirth(profile.getDateOfBirth())
                .gender(profile.getGender())
                .phoneNumber(profile.getPhoneNumber())
                .address(profile.getAddress())
                .bloodGroup(profile.getBloodGroup())
                .allergies(profile.getAllergies())
                .chronicConditions(profile.getChronicConditions())
                .emergencyContactName(profile.getEmergencyContactName())
                .emergencyContactPhone(profile.getEmergencyContactPhone())
                .profilePictureUrl(profile.getUser().getProfilePictureUrl())
                .build();
    }
}
