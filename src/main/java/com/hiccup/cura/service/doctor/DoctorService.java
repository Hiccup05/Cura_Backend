package com.hiccup.cura.service.doctor;

import com.hiccup.cura.dto.reqeust.DoctorRequestDto;
import com.hiccup.cura.dto.response.DoctorDto;
import com.hiccup.cura.dto.response.MessageResponseDto;
import com.hiccup.cura.enums.DoctorStatus;
import com.hiccup.cura.exception.custom.DublicateEntryException;
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

import javax.print.Doc;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class DoctorService {
    private final DoctorRepository doctorRepository;
    private final UserRepository userRepository;
    private final SpecializationRepository specializationRepository;

    @Transactional
    public DoctorDto createDoctor (Long userId, DoctorRequestDto doctorRequestDto) throws Exception{
       if(doctorRepository.existsByUserId(userId)){
           throw new DublicateEntryException("Doctor with id "+ userId+ "already exist");
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
        if (request.getYearsOfExperience() != 0) {
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

    public MessageResponseDto deleteDoctor(Long id){
        DoctorProfile doctorProfile=doctorRepository.findById(id).orElseThrow(()->new ResourceNotFoundException("Doctor Not found by id "+id));
        doctorRepository.delete(doctorProfile);
        return new MessageResponseDto("Doctor profile is deleted with id+ "+id, LocalDateTime.now());
    }

    private DoctorDto mapToResponseDto(DoctorProfile doctorProfile){
        return DoctorDto.builder().doctorStatus(doctorProfile.getDoctorStatus())
                .specialization(doctorProfile.getSpecialization())
                .licenseNumber(doctorProfile.getLicenseNumber())
                .yearsOfExperience(doctorProfile.getYearsOfExperience())
                .build();
    }


}
