package com.hiccup.cura.mapper;

import com.hiccup.cura.dto.response.ReceptionistResponseDto;
import com.hiccup.cura.model.ReceptionistProfile;
import org.springframework.stereotype.Component;

@Component
public class ReceptionistProfileMapper {

    public ReceptionistResponseDto toDto(ReceptionistProfile profile) {
        return ReceptionistResponseDto.builder()
                .id(profile.getId())
                .firstName(profile.getFirstName())
                .lastName(profile.getLastName())
                .phoneNumber(profile.getPhoneNumber())
                .status(profile.getStatus())
                .profilePictureUrl(profile.getUser().getProfilePictureUrl())
                .build();
    }
}
