package com.hiccup.cura.service.patient;

import com.hiccup.cura.dto.reqeust.PatientRequestDto;
import com.hiccup.cura.dto.response.PatientResponseDto;
import com.hiccup.cura.exception.custom.DuplicateEntryException;
import com.hiccup.cura.exception.custom.ResourceNotFoundException;
import com.hiccup.cura.model.PatientProfile;
import com.hiccup.cura.model.User;
import com.hiccup.cura.repository.PatientRepository;
import com.hiccup.cura.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PatientService {
    private final PatientRepository patientRepository;
    private final UserRepository userRepository;

    public PatientResponseDto create(Long userId){
        if(patientRepository.existsById(userId)){throw new DuplicateEntryException("Patient not found");}
        User user = userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("User not found with id " + userId));
        PatientProfile patientProfile = new PatientProfile();
        patientProfile.setUser(user);

        return mapToDto(patientRepository.save(patientProfile));
    }

    public PatientResponseDto getById(Long id){
        PatientProfile patientProfile = patientRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Patient not found with id " + id));
        return mapToDto(patientProfile);
    }



    private PatientResponseDto mapToDto(PatientProfile profile){
        return PatientResponseDto.builder()
                .id(profile.getId())
                .firstName(profile.getFirstName())
                .lastName(profile.getLastName())
                .dateOfBirth(profile.getDateOfBirth())
                .gender(profile.getGender())
                .phoneNumber(profile.getPhoneNumber())
                .address(profile.getAddress())
                .bloodGroup(profile.getBloodGroup())
                .allergies(profile.getAllergies())
                .chronicConditions(profile.getChronicConditions())
                .emergencyContactName(profile.getEmergencyContactName())
                .emergencyContactPhone(profile.getEmergencyContactPhone())
                .build();
    }

}
