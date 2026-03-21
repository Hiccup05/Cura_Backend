package com.hiccup.cura.service.doctor;

import com.hiccup.cura.dto.reqeust.ChangeStatusRequestDto;
import com.hiccup.cura.dto.reqeust.DoctorRequestDto;
import com.hiccup.cura.dto.response.DoctorDto;
import com.hiccup.cura.dto.response.MessageResponseDto;
import com.hiccup.cura.enums.DoctorStatus;
import com.hiccup.cura.exception.custom.DuplicateEntryException;
import com.hiccup.cura.exception.custom.ResourceNotFoundException;
import com.hiccup.cura.model.DoctorProfile;
import com.hiccup.cura.model.Specialization;
import com.hiccup.cura.model.User;
import com.hiccup.cura.repository.DoctorRepository;
import com.hiccup.cura.repository.SpecializationRepository;
import com.hiccup.cura.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class DoctorService {
    private final DoctorRepository doctorRepository;
    private final UserRepository userRepository;
    private final SpecializationRepository specializationRepository;

    @Transactional
    public DoctorDto createDoctor (Long userId, DoctorRequestDto doctorRequestDto){
       if(doctorRepository.existsByUserId(userId)){
           throw new DuplicateEntryException("Doctor with id "+ userId+ "already exist");
       }
        User user=userRepository.findById(userId).orElseThrow(()->new ResourceNotFoundException("User doesn't exists with id "+ userId));
       Set<Specialization> specializationSet=new HashSet<>(
               specializationRepository.findAllById(doctorRequestDto.getSpecializationIds())
       );
       DoctorProfile doctorProfile=new DoctorProfile();
        doctorProfile.setUser(user);
        doctorProfile.setSpecialization(specializationSet);
        doctorProfile.setDoctorStatus(DoctorStatus.ACTIVE);
        doctorProfile.setLicenseNumber(doctorRequestDto.getLicenseNumber());
        doctorProfile.setYearsOfExperience(doctorRequestDto.getYearsOfExperience());

        DoctorProfile saveDoctor = doctorRepository.save(doctorProfile);

        return mapToResponseDto(saveDoctor);
    }

    public List<DoctorDto> getDoctors(){
        return doctorRepository.findAll().stream()
                .map(this::mapToResponseDto).toList();
    }

    public DoctorDto getDoctor(Long id){
        DoctorProfile doctorProfile=doctorRepository.findById(id).orElseThrow(()->new ResourceNotFoundException("Doctor Not found by id "+id));
        return mapToResponseDto(doctorProfile);
    }

    public DoctorDto updateDoctor(Long id, DoctorRequestDto request){
        DoctorProfile doctor=doctorRepository.findById(id).orElseThrow(()->new ResourceNotFoundException("Doctor Not found by id "+id));
        if (request.getLicenseNumber() != null) {
            doctor.setLicenseNumber(request.getLicenseNumber());
        }
        if (request.getYearsOfExperience() != null) {
            doctor.setYearsOfExperience(request.getYearsOfExperience());
        }
        if (request.getSpecializationIds() != null && !request.getSpecializationIds().isEmpty()) {
            Set<Specialization> specializations = new HashSet<>(
                    specializationRepository.findAllById(request.getSpecializationIds())
            );
            doctor.setSpecialization(specializations);
        }

        return mapToResponseDto(doctorRepository.save(doctor));
    }

    public void deleteDoctor(Long id){
        DoctorProfile doctorProfile=doctorRepository.findById(id).orElseThrow(()->new ResourceNotFoundException("Doctor Not found by id "+id));
        DoctorProfile doctor = doctorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Doctor not found with id " + id));

        if (doctor.getDoctorStatus() == DoctorStatus.ACTIVE) {
            throw new IllegalStateException("Cannot delete an active doctor. Deactivate first.");
        }
        doctorRepository.delete(doctorProfile);
    }

    @Transactional
    public DoctorDto changeStatus(Long id, ChangeStatusRequestDto changeStatusRequestDto){
        DoctorProfile doctorProfile=doctorRepository.findById(id).orElseThrow(()->new ResourceNotFoundException("Doctor Not found by id "+id));
        doctorProfile.setDoctorStatus(changeStatusRequestDto.getDoctorStatus());
        return mapToResponseDto(doctorRepository.save(doctorProfile));
    }

    private DoctorDto mapToResponseDto(DoctorProfile doctorProfile){
        return DoctorDto.builder().doctorStatus(doctorProfile.getDoctorStatus())
                .specialization(doctorProfile.getSpecialization())
                .licenseNumber(doctorProfile.getLicenseNumber())
                .yearsOfExperience(doctorProfile.getYearsOfExperience())
                .build();
    }
}
