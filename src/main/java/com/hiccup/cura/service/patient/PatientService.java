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


}
