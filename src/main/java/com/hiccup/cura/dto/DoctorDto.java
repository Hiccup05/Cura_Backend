package com.hiccup.cura.dto;

import com.hiccup.cura.enums.DoctorStatus;
import com.hiccup.cura.model.Specialization;
import com.hiccup.cura.model.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DoctorDto {
    private User user;
    private Set<Specialization> specialization;
    private int  yearsOfExperience;
    private String licenseNumber;
    private DoctorStatus doctorStatus;
}
