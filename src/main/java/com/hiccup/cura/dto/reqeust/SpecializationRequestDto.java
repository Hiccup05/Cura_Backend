package com.hiccup.cura.dto.reqeust;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SpecializationRequestDto {
    @NotBlank(message = "Specialization name cannot be blank")
    private String name;
}
