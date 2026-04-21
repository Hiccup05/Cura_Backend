package com.hiccup.cura.controller.publics;

import com.hiccup.cura.dto.response.MedicalServiceResponseDto;
import com.hiccup.cura.service.medicalservice.MedicalServiceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("${api.prefix}/public/service")
@RequiredArgsConstructor
public class ServiceController {
    private final MedicalServiceService serviceService;

    @GetMapping
    public ResponseEntity<List<MedicalServiceResponseDto>> getAllServices(){
        return ResponseEntity.ok(serviceService.getActiveServices());
    }

    @GetMapping("/search")
    public ResponseEntity<List<MedicalServiceResponseDto>> searchByName(@RequestParam String name){
        return ResponseEntity.ok(serviceService.searchByName(name));
    }

    @GetMapping("/{specializationId}")
    public ResponseEntity<List<MedicalServiceResponseDto>> getAllServices(@PathVariable Long specializationId){
        return ResponseEntity.ok(serviceService.getActiveSpecializationServices(specializationId));
    }

    @GetMapping("/{id}/photo")
    public ResponseEntity<String> getServicePhoto(@PathVariable Long id){
        return ResponseEntity.ok(serviceService.getPhoto(id));
    }
}
