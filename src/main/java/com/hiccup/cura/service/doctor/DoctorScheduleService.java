package com.hiccup.cura.service.doctor;

import com.hiccup.cura.dto.reqeust.ScheduleRequestDto;
import com.hiccup.cura.dto.reqeust.ScheduleUpdateRequestDto;
import com.hiccup.cura.dto.response.PublicScheduleResponseDto;
import com.hiccup.cura.dto.response.ScheduleResponseDto;
import com.hiccup.cura.exception.custom.DuplicateEntryException;
import com.hiccup.cura.exception.custom.ResourceNotFoundException;
import com.hiccup.cura.model.DoctorProfile;
import com.hiccup.cura.model.DoctorSchedule;
import com.hiccup.cura.repository.DoctorRepository;
import com.hiccup.cura.repository.DoctorScheduleRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
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
        doctorSchedule.setDoctorProfile(doctor);
        doctorSchedule.setStartTime(scheduleRequestDto.getStartTime());
        doctorSchedule.setEndTime(scheduleRequestDto.getEndTime());
        doctorSchedule.setDayOfWeek(scheduleRequestDto.getDayOfWeek());
        doctorSchedule.setMaxAppointments(scheduleRequestDto.getMaxAppointments());
        doctorSchedule.setIsAvailable(true);

        return mapToDto(scheduleRepository.save(doctorSchedule));
    }

    public List<ScheduleResponseDto> getSchedulesOfDoctor(Long doctorId){
        if(!doctorRepository.existsById(doctorId)){
            throw new ResourceNotFoundException("Doctor cannot be found with id "+ doctorId);
        }
        List<DoctorSchedule> byDoctorProfileId = scheduleRepository.findByDoctorProfile_id(doctorId);

        return byDoctorProfileId.stream().map( this::mapToDto).toList();
    }

    public ScheduleResponseDto getScheduleOfDoctor(Long doctorId, Long scheduleId){
        if(!doctorRepository.existsById(doctorId)){
            throw new ResourceNotFoundException("Doctor cannot be found with id "+ doctorId);
        }
        DoctorSchedule byDoctorProfileId = scheduleRepository.findByIdAndDoctorProfile_id(scheduleId, doctorId);

        return mapToDto(byDoctorProfileId);
    }

    public DoctorSchedule getScheduleFromDay(DoctorProfile doctor, DayOfWeek dayOfWeek){
        return scheduleRepository.findByDayOfWeekAndDoctorProfile_Id(dayOfWeek, doctor.getId()).orElseThrow(() ->
                new ResourceNotFoundException("Doctor does not have the schedule for this appointment day")
        );
    }

    public List<PublicScheduleResponseDto> getPublicSchedulesOfDoctor(Long doctorId){
        if(!doctorRepository.existsById(doctorId)){
            throw new ResourceNotFoundException("Doctor cannot be found with id "+ doctorId);
        }
        List<DoctorSchedule> byDoctorProfileId = scheduleRepository.findByDoctorProfile_id(doctorId);

        return byDoctorProfileId.stream()
                .filter(DoctorSchedule::getIsAvailable)
                .map(schedule->
                    PublicScheduleResponseDto.builder().doctorId(schedule.getDoctorProfile().getId())
                            .doctorName(schedule.getDoctorProfile().getUser().getUsername())
                            .id(schedule.getId())
                            .startTime(schedule.getStartTime())
                            .endTime(schedule.getEndTime())
                            .dayOfWeek(schedule.getDayOfWeek())
                            .build()).toList();
    }

    @Transactional
    public ScheduleResponseDto updateScheduleOfDoctor(ScheduleUpdateRequestDto updateRequestDto, Long doctorId, Long scheduleId){
        DoctorSchedule validatedSchedule = getValidatedSchedule(doctorId, scheduleId);

        if(updateRequestDto.getDayOfWeek()!=null){
            validatedSchedule.setDayOfWeek(updateRequestDto.getDayOfWeek());
        }
        if(updateRequestDto.getStartTime()!=null){
            validatedSchedule.setStartTime(updateRequestDto.getStartTime());
        }
        if(updateRequestDto.getEndTime()!=null){
            validatedSchedule.setEndTime(updateRequestDto.getEndTime());
        }
        if(updateRequestDto.getMaxAppointments()!=null){
            validatedSchedule.setMaxAppointments(updateRequestDto.getMaxAppointments());
        }
        return mapToDto(scheduleRepository.save(validatedSchedule));
    }

    @Transactional
    public ScheduleResponseDto toggleScheduleOfDoctor(Long doctorId, Long scheduleId){
        DoctorSchedule validatedSchedule = getValidatedSchedule(doctorId, scheduleId);
        validatedSchedule.setIsAvailable(!validatedSchedule.getIsAvailable());
        return  mapToDto(scheduleRepository.save(validatedSchedule));
    }

    @Transactional
    public void deleteDoctorSchedule(Long doctorId, Long scheduleId){
        getValidatedSchedule(doctorId, scheduleId);
        scheduleRepository.deleteById(scheduleId);
    }

    private DoctorSchedule getValidatedSchedule(Long doctorId, Long scheduleId) {
        if (!doctorRepository.existsById(doctorId)) {
            throw new ResourceNotFoundException("Doctor not found with id " + doctorId);
        }
        DoctorSchedule schedule = scheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new ResourceNotFoundException("Schedule not found with id " + scheduleId));
        if (!schedule.getDoctorProfile().getId().equals(doctorId)) {
            throw new ResourceNotFoundException("Schedule does not belong to doctor with id " + doctorId);
        }
        return schedule;
    }

    public ScheduleResponseDto mapToDto(DoctorSchedule schedule){
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
