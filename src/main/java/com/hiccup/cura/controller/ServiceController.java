package com.hiccup.cura.controller;

import com.hiccup.cura.dto.response.MedicalServiceResponseDto;
import com.hiccup.cura.service.medicalservice.MedicalServiceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("${api.prefix}/service")
@RequiredArgsConstructor
public class ServiceController {
    private final MedicalServiceService serviceService;

    @GetMapping
    public ResponseEntity<List<MedicalServiceResponseDto>> getAllServices(){
        return ResponseEntity.ok(serviceService.getActiveServices());
    }

    @GetMapping("/{specializationId}")
    public ResponseEntity<List<MedicalServiceResponseDto>> getAllServices(@PathVariable Long specializationId){
        return ResponseEntity.ok(serviceService.getActiveSpecializationServices(specializationId));
    }
}
