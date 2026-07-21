package com.hiccup.cura.controller.publics;

import com.hiccup.cura.dto.response.MedicalServiceResponseDto;
import com.hiccup.cura.dto.response.SpecializationDto;
import com.hiccup.cura.service.MedicalServiceService;
import com.hiccup.cura.service.SpecializationService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("${api.prefix}/public/specializations")
@RequiredArgsConstructor
@Tag(name="Specialization", description = "About the specialization that doctors in CURA have.")
public class SpecializationController {
    private final SpecializationService service;
    private final MedicalServiceService medicalServiceService;

    @GetMapping
    public ResponseEntity<List<SpecializationDto>> getAll(){
        return ResponseEntity.ok(service.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<SpecializationDto> getOne(@PathVariable Long id){
        return ResponseEntity.ok(service.getById(id));
    }

    @GetMapping("/{id}/services")
    public ResponseEntity<List<MedicalServiceResponseDto>> getSpecializationServices(@PathVariable Long id){
        return ResponseEntity.ok(medicalServiceService.getActiveSpecializationServices(id));
    }
}
