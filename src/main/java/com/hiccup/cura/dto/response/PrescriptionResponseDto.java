package com.hiccup.cura.dto.response;

import com.hiccup.cura.model.Prescription;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PrescriptionResponseDto {
    private Long id;
    private String description;
}
