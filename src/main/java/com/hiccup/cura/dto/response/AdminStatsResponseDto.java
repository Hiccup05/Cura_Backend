package com.hiccup.cura.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AdminStatsResponseDto {
    private long totalDoctors;
    private long totalPatients;
    private long totalAppointments;
    private long pendingDoctorApprovals;
    private BigDecimal totalRevenue;
}
