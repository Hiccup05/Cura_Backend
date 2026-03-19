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

    public ScheduleResponseDto createSchedule(ScheduleResponseDto scheduleRequestDto, Long doctorId){
        DoctorProfile doctor=doctorRepository.findById(doctorId).orElseThrow(()->new ResourceNotFoundException("Doctor cannot be found with id "+ doctorId));
        if(scheduleRepository.existsByDayOfWeek(scheduleRequestDto.getDayOfWeek())){
            throw new DuplicateEntryException("Schedule in "+scheduleRequestDto.getDayOfWeek()+" already exists for the doctor with id "+doctorId);
        }
        DoctorSchedule doctorSchedule=scheduleRepository.save(new DoctorSchedule());
        doctorSchedule.setDoctor(doctor);
        doctorSchedule.setStartTime(scheduleRequestDto.getStartTime());
        doctorSchedule.setEndTime(scheduleRequestDto.getEndTime());
        doctorSchedule.setDayOfWeek(scheduleRequestDto.getDayOfWeek());
        doctorSchedule.setMaxAppointments(scheduleRequestDto.getMaxAppointments());

        return mapToDto(scheduleRepository.save(doctorSchedule));
    }

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
