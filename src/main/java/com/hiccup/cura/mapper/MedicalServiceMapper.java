package com.hiccup.cura.mapper;

import com.hiccup.cura.dto.response.MedicalServiceResponseDto;
import com.hiccup.cura.model.MedicalService;
import org.springframework.stereotype.Component;

@Component
public class MedicalServiceMapper {

    public MedicalServiceResponseDto toDto(MedicalService service) {
        return new MedicalServiceResponseDto(
                service.getId(), service.getName(), service.getPrice(), service.getDurationMinutes(),
                service.getDescription(), service.getIsActive(), service.getSpecialization().getId(),
                service.getSpecialization().getName(), service.getPhotoUrl());
    }
}
