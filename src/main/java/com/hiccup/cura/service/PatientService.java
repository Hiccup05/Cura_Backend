package com.hiccup.cura.service;

import com.hiccup.cura.dto.reqeust.PatientRequestDto;
import com.hiccup.cura.dto.response.PatientResponseDto;
import com.hiccup.cura.exception.custom.DuplicateEntryException;
import com.hiccup.cura.exception.custom.ResourceNotFoundException;
import com.hiccup.cura.mapper.PatientProfileMapper;
import com.hiccup.cura.model.PatientProfile;
import com.hiccup.cura.model.User;
import com.hiccup.cura.repository.PatientRepository;
import com.hiccup.cura.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PatientService {
    private final PatientRepository patientRepository;
    private final UserRepository userRepository;
    private final PatientProfileMapper patientProfileMapper;

    @Transactional
    public PatientResponseDto create(Long userId){
        if(patientRepository.existsById(userId)){throw new DuplicateEntryException("Patient not found");}
        User user = userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("User not found with id " + userId));
        PatientProfile patientProfile = new PatientProfile();
        patientProfile.setUser(user);

        return patientProfileMapper.toDto(patientRepository.save(patientProfile));
    }

    public PatientResponseDto getById(Long id){
        PatientProfile patientProfile = patientRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Patient not found with id " + id));
        return patientProfileMapper.toDto(patientProfile);
    }

    public PatientResponseDto updateById(Long id, PatientRequestDto patientRequestDto){
        PatientProfile patientProfile = patientRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("User not found with id " + id));
        if (patientRequestDto.getFirstName() != null) {
            patientProfile.setFirstName(patientRequestDto.getFirstName());
        }
        if (patientRequestDto.getLastName() != null) {
            patientProfile.setLastName(patientRequestDto.getLastName());
        }
        if (patientRequestDto.getDateOfBirth() != null) {
            patientProfile.setDateOfBirth(patientRequestDto.getDateOfBirth());
        }
        if (patientRequestDto.getGender() != null) {
            patientProfile.setGender(patientRequestDto.getGender());
        }
        if (patientRequestDto.getPhoneNumber() != null) {
            patientProfile.setPhoneNumber(patientRequestDto.getPhoneNumber());
        }
        if (patientRequestDto.getAddress() != null) {
            patientProfile.setAddress(patientRequestDto.getAddress());
        }
        if (patientRequestDto.getBloodGroup() != null) {
            patientProfile.setBloodGroup(patientRequestDto.getBloodGroup());
        }
        if (patientRequestDto.getAllergies() != null) {
            patientProfile.setAllergies(patientRequestDto.getAllergies());
        }
        if (patientRequestDto.getChronicConditions() != null) {
            patientProfile.setChronicConditions(patientRequestDto.getChronicConditions());
        }
        if (patientRequestDto.getEmergencyContactName() != null) {
            patientProfile.setEmergencyContactName(patientRequestDto.getEmergencyContactName());
        }
        if (patientRequestDto.getEmergencyContactPhone() != null) {
            patientProfile.setEmergencyContactPhone(patientRequestDto.getEmergencyContactPhone());
        }
        return patientProfileMapper.toDto(patientRepository.save(patientProfile));
    }

    public void deleteById(Long id){
        if(patientRepository.existsById(id)){
            patientRepository.deleteById(id);
        }else {
            throw new ResourceNotFoundException("Patient not found with id " + id);
        }
    }

}
