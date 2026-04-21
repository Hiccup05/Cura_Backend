package com.hiccup.cura.dto.response;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.context.annotation.ApplicationScope;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MedicalServiceResponseDto {
    private Long id;
    private String name;
    private BigDecimal price;
    private Integer durationMinutes;
    private String description;
    private Boolean isActive;
    private Long specializationId;
    private String specializationName;
    private String photoUrl;
}
