package com.hiccup.cura.service;

import com.hiccup.cura.dto.reqeust.ScheduleRequestDto;
import com.hiccup.cura.dto.response.ScheduleResponseDto;
import com.hiccup.cura.exception.custom.DuplicateEntryException;
import com.hiccup.cura.exception.custom.ResourceNotFoundException;
import com.hiccup.cura.model.DoctorProfile;
import com.hiccup.cura.model.DoctorSchedule;
import com.hiccup.cura.repository.DoctorRepository;
import com.hiccup.cura.repository.DoctorScheduleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DoctorScheduleService {
    private final DoctorScheduleRepository scheduleRepository;
    private final DoctorRepository doctorRepository;

    public ScheduleResponseDto createSchedule(ScheduleRequestDto scheduleRequestDto, Long doctorId){
        DoctorProfile doctor=doctorRepository.findById(doctorId).orElseThrow(()->new ResourceNotFoundException("Doctor cannot be found with id "+ doctorId));
        if(scheduleRepository.existsByDayOfWeekAndDoctorProfile_Id(scheduleRequestDto.getDayOfWeek(), doctorId)){
            throw new DuplicateEntryException("Schedule in "+scheduleRequestDto.getDayOfWeek()+" already exists for the doctor with id "+doctorId);
        }
        DoctorSchedule doctorSchedule=new DoctorSchedule();
        doctorSchedule.setDoctor(doctor);
        doctorSchedule.setStartTime(scheduleRequestDto.getStartTime());
        doctorSchedule.setEndTime(scheduleRequestDto.getEndTime());
        doctorSchedule.setDayOfWeek(scheduleRequestDto.getDayOfWeek());
        doctorSchedule.setMaxAppointments(scheduleRequestDto.getMaxAppointments());
        doctorSchedule.setIsAvailable(true);

        return mapToDto(scheduleRepository.save(doctorSchedule));
    }

    public List<ScheduleResponseDto> getSchedulesOfDoctor(Long doctorId){
        if(!doctorRepository.existsByUserId(doctorId)){
            throw new ResourceNotFoundException("Doctor cannot be found with id "+ doctorId);
        }
        List<DoctorSchedule> byDoctorProfileId = scheduleRepository.findByDoctorProfile_id(doctorId);

        return byDoctorProfileId.stream().map( this::mapToDto).toList();
    }

    public ScheduleResponseDto updateScheduleOfDoctor(ScheduleRequestDto scheduleRequestDto){

    }

    public ScheduleResponseDto mapToDto(DoctorSchedule schedule){
        return ScheduleResponseDto.builder()
                .id(schedule.getId())
                .doctorId(schedule.getDoctor().getId())
                .doctorName(schedule.getDoctor().getUser().getUsername())
                .maxAppointments(schedule.getMaxAppointments())
                .endTime(schedule.getEndTime())
                .startTime(schedule.getStartTime())
                .dayOfWeek(schedule.getDayOfWeek())
                .isAvailable(schedule.getIsAvailable())
                .build();
    }
}
