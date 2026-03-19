package com.hiccup.cura.dto.response;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
public class MedicalServiceResponseDto {
    private Long id;
    private String name;
    private BigDecimal price;
    private Integer durationMinutes;
    private String description;
    private Boolean isActive;
    private Long specializationId;
    private String specializationName;

    public MedicalServiceResponseDto(Long id, String name, BigDecimal price, Integer durationMinutes, String description, Boolean isActive, Long specializationId, String specializationName) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.durationMinutes = durationMinutes;
        this.description = description;
        this.isActive = isActive;
        this.specializationId = specializationId;
        this.specializationName = specializationName;
    }
}
