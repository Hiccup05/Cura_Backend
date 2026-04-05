package com.hiccup.cura.dto.reqeust;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DoctorRequestDto {
    private Set<Long> specializationIds;
    private Integer  yearsOfExperience;
    private String licenseNumber;
}
