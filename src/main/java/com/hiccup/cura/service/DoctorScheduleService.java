package com.hiccup.cura.service;

import com.hiccup.cura.dto.response.ScheduleResponseDto;
import com.hiccup.cura.exception.custom.DuplicateEntryException;
import com.hiccup.cura.exception.custom.ResourceNotFoundException;
import com.hiccup.cura.model.DoctorProfile;
import com.hiccup.cura.model.DoctorSchedule;
import com.hiccup.cura.repository.DoctorRepository;
import com.hiccup.cura.repository.DoctorScheduleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DoctorScheduleService {
    private final DoctorScheduleRepository scheduleRepository;
    private final DoctorRepository doctorRepository;


    public ScheduleResponseDto mapToDto(DoctorSchedule schedule){
        return ScheduleResponseDto.builder()
                .id(schedule.getId())
                .doctorId(schedule.getDoctor().getId())
                .maxAppointments(schedule.getMaxAppointments())
                .endTime(schedule.getEndTime())
                .startTime(schedule.getStartTime())
                .dayOfWeek(schedule.getDayOfWeek())
                .isAvailable(schedule.getIsAvailable())
                .build();
    }
}
