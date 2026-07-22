package com.hiccup.cura.dto.request;

import com.hiccup.cura.model.DoctorProfile;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LeaveRequestDto {
    private LocalDate startDate;
    private LocalDate endDate;
    private String reason;
}
