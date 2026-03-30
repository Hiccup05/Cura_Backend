package com.hiccup.cura.dto.response;

import com.hiccup.cura.enums.DoctorStatus;
import com.hiccup.cura.model.Specialization;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PublicDoctorResponseDto {
    private Long id;
    private String firstName;
    private String lastName;
    private Set<Specialization> specialization;
    private int  yearsOfExperience;
    private String licenseNumber;
    private DoctorStatus doctorStatus;
}
