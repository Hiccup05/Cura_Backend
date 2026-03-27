package com.hiccup.cura.service.doctor;

import com.hiccup.cura.dto.reqeust.LeaveRequestDto;
import com.hiccup.cura.dto.response.LeaveResponseDto;
import com.hiccup.cura.exception.custom.DuplicateEntryException;
import com.hiccup.cura.exception.custom.ResourceNotFoundException;
import com.hiccup.cura.model.DoctorLeave;
import com.hiccup.cura.model.DoctorProfile;
import com.hiccup.cura.repository.DoctorLeaveRepository;
import com.hiccup.cura.repository.DoctorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class LeaveService {
    private final DoctorLeaveRepository doctorLeaveRepository;
    private final DoctorRepository doctorRepository;

    public LeaveResponseDto getLeave(Long doctorId, Long leaveId){
        if(!doctorRepository.existsById(doctorId)){
            throw new ResourceNotFoundException("Doctor cannot be found with id " + doctorId);
        }
        DoctorLeave doctorLeave = doctorLeaveRepository.findById(leaveId).orElseThrow(() -> new ResourceNotFoundException("Doctor Leave cannot be found with id " + leaveId));
        if(!Objects.equals(doctorLeave.getDoctorProfile().getId(), doctorId)){
            throw new ResourceNotFoundException("Doctor with id "+doctorId+" doesnt match doctor id of leave with id "+ leaveId);
        }
        return new LeaveResponseDto(doctorLeave.getId(), doctorLeave.getStartDate(), doctorLeave.getEndDate(), doctorLeave.getReason(),
                doctorLeave.getDoctorProfile().getId());
    }

    public LeaveResponseDto createLeave(Long doctorId,LeaveRequestDto leaveRequestDto) {
        DoctorProfile doctorProfile = doctorRepository.findById(doctorId).orElseThrow(() -> new ResourceNotFoundException("Doctor cannot be found with id " + doctorId));
        if(doctorLeaveRepository.existsOverlappingLeave(doctorId,leaveRequestDto.getStartDate(),leaveRequestDto.getEndDate())) {
            throw new DuplicateEntryException("Doctor Leave is overlapped with the current start date and end date");
        }
        DoctorLeave doctorLeave = new DoctorLeave();
        doctorLeave.setDoctorProfile(doctorProfile);
        doctorLeave.setStartDate(leaveRequestDto.getStartDate());
        doctorLeave.setEndDate(leaveRequestDto.getEndDate());
        doctorLeave.setReason(leaveRequestDto.getReason());
        doctorLeave= doctorLeaveRepository.save(doctorLeave);
        return new LeaveResponseDto(doctorLeave.getId(), doctorLeave.getStartDate(), doctorLeave.getEndDate(), doctorLeave.getReason(),
                doctorLeave.getDoctorProfile().getId());
    }


}
