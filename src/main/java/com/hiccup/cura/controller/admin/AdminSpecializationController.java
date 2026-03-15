package com.hiccup.cura.controller.admin;

import com.hiccup.cura.dto.reqeust.SpecializationRequestDto;
import com.hiccup.cura.dto.response.MessageResponseDto;
import com.hiccup.cura.model.Specialization;
import com.hiccup.cura.service.doctor.specialization.SpecializationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("${api.prefix}/admin/specialization")
@RequiredArgsConstructor
public class AdminSpecializationController {
    private final SpecializationService service;

    public ResponseEntity<Specialization> createSpecialization(@Valid @RequestBody  SpecializationRequestDto specializationRequestDto){
        Specialization created =service.create(specializationRequestDto);
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(created.getId())
                .toUri();
        return ResponseEntity.created(location).body(created);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<MessageResponseDto> createSpecialization(@PathVariable Long id){
        return ResponseEntity.ok(service.delete(id));
    }
}
