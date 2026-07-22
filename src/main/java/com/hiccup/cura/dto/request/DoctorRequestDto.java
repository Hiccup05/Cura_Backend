package com.hiccup.cura.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DoctorRequestDto {
    @NotNull(message = "userId of the user to promote is required")
    private Long userId;
    @NotEmpty(message = "at least one specializationId is required")
    private Set<Long> specializationIds;
    @NotNull(message = "yearsOfExperience is required")
    @PositiveOrZero(message = "yearsOfExperience cannot be negative")
    private Integer  yearsOfExperience;
    @NotBlank(message = "licenseNumber is required")
    private String licenseNumber;
    @NotBlank(message = "firstName is required")
    private String firstName;
    @NotBlank(message = "lastName is required")
    private String lastName;
}
