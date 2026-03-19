package com.hiccup.cura.dto.response;

import lombok.*;

import java.time.DayOfWeek;
import java.time.LocalTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ScheduleResponseDto {
    private Long id;
    private Long doctorId;
    private String doctorName;
    private DayOfWeek dayOfWeek;
    private LocalTime startTime;
    private LocalTime endTime;
    private Integer maxAppointments;
    private Boolean isAvailable;
}
