package com.hiccup.cura.mapper;

import com.hiccup.cura.dto.response.PrescriptionResponseDto;
import com.hiccup.cura.model.Prescription;
import org.springframework.stereotype.Component;

@Component
public class PrescriptionMapper {

    public PrescriptionResponseDto toDto(Prescription prescription) {
        return new PrescriptionResponseDto(prescription.getId(), prescription.getDescription());
    }
}
