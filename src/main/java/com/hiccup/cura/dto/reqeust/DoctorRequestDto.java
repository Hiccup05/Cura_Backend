package com.hiccup.cura.dto.reqeust;

import com.hiccup.cura.enums.DoctorStatus;
import com.hiccup.cura.model.Specialization;
import com.hiccup.cura.model.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DoctorRequestDto {
//    private Long userId;
    private Set<Long> specializationIds;
    private Integer  yearsOfExperience;
    private String licenseNumber;
//    private DoctorStatus doctorStatus;
}
