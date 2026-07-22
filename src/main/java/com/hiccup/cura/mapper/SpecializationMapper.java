package com.hiccup.cura.mapper;

import com.hiccup.cura.dto.response.SpecializationDto;
import com.hiccup.cura.model.Specialization;
import org.springframework.stereotype.Component;

@Component
public class SpecializationMapper {

    public SpecializationDto toDto(Specialization specialization) {
        return new SpecializationDto(
                specialization.getId(), specialization.getName(), specialization.getDescription());
    }
}
