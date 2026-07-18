package com.hiccup.cura.mapper;

import com.hiccup.cura.dto.response.ScheduleResponseDto;
import com.hiccup.cura.model.DoctorSchedule;
import org.springframework.stereotype.Component;

@Component
public class DoctorScheduleMapper {

    public ScheduleResponseDto toDto(DoctorSchedule schedule) {
        return ScheduleResponseDto.builder()
                .id(schedule.getId())
                .doctorId(schedule.getDoctorProfile().getId())
                .doctorName(schedule.getDoctorProfile().getUser().getUsername())
                .maxAppointments(schedule.getMaxAppointments())
                .endTime(schedule.getEndTime())
                .startTime(schedule.getStartTime())
                .dayOfWeek(schedule.getDayOfWeek())
                .isAvailable(schedule.getIsAvailable())
                .build();
    }
}
