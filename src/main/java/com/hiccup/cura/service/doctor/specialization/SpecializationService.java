package com.hiccup.cura.service.doctor.specialization;

import com.hiccup.cura.dto.reqeust.SpecializationRequestDto;
import com.hiccup.cura.dto.response.MessageResponseDto;
import com.hiccup.cura.exception.custom.DuplicateEntryException;
import com.hiccup.cura.exception.custom.ResourceNotFoundException;
import com.hiccup.cura.model.Specialization;
import com.hiccup.cura.repository.SpecializationRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SpecializationService {
    private final SpecializationRepository specializationRepository;

    public List<Specialization> getAll() {
        return specializationRepository.findAll();
    }

    public Specialization getById(Long id) {
        return specializationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Specialization not found with id " + id));
    }

    @Transactional
    public Specialization create(SpecializationRequestDto requestDto) {
        if (specializationRepository.existsByName(requestDto.getName())) {
            throw new DuplicateEntryException("Specialization already exists: " + requestDto.getName());
        }
        Specialization specialization = new Specialization();
        specialization.setName(requestDto.getName());
        return specializationRepository.save(specialization);
    }

    @Transactional
    public MessageResponseDto delete(Long id) {
        Specialization specialization = getById(id);
        specializationRepository.delete(specialization);
        return new MessageResponseDto("Specialization deleted with id " + id, LocalDateTime.now());
    }
}
