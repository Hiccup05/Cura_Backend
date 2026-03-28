package com.hiccup.cura.service;

import com.hiccup.cura.dto.reqeust.ReceptionistRequestDto;
import com.hiccup.cura.dto.response.ReceptionistResponseDto;
import com.hiccup.cura.enums.RoleType;
import com.hiccup.cura.exception.custom.DuplicateEntryException;
import com.hiccup.cura.exception.custom.ResourceNotFoundException;
import com.hiccup.cura.model.ReceptionistProfile;
import com.hiccup.cura.model.User;
import com.hiccup.cura.repository.ReceptionistRepository;
import com.hiccup.cura.repository.RoleRepository;
import com.hiccup.cura.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class ReceptionistService {
    private final ReceptionistRepository receptionistRepository;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    @Transactional
    public ReceptionistResponseDto createReceptionist(Long userId, ReceptionistRequestDto requestDto) {
        if (receptionistRepository.existsById(userId)) {
            throw new DuplicateEntryException("Receptionist with id " + userId + " already exists");
        }
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User doesn't exist with id " + userId));

        user.setRole(Set.of(roleRepository.findByName(RoleType.RECEPTIONIST)));

        ReceptionistProfile receptionistProfile = new ReceptionistProfile();
        receptionistProfile.setUser(user);
        receptionistProfile.setFirstName(requestDto.getFirstName());
        receptionistProfile.setLastName(requestDto.getLastName());
        receptionistProfile.setPhoneNumber(requestDto.getPhoneNumber());

        return mapToDto(receptionistRepository.save(receptionistProfile));
    }



    private ReceptionistResponseDto mapToDto(ReceptionistProfile profile) {
        return ReceptionistResponseDto.builder()
                .id(profile.getId())
                .firstName(profile.getFirstName())
                .lastName(profile.getLastName())
                .phoneNumber(profile.getPhoneNumber())
                .build();
    }
}
