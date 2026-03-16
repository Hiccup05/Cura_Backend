package com.hiccup.cura.dto.reqeust;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MedicalServiceRequestDto {
    private String name;
    private BigDecimal price;
    private Integer durationMinutes;
    private String description;
    private Boolean isActive;
    private Long specializationId;
}
