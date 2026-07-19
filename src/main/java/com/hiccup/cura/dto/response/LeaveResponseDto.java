package com.hiccup.cura.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LeaveResponseDto {
    private Long id;
    private LocalDate startDate;
    private LocalDate endDate;
    private String reason;
    private Long doctorId;
}
