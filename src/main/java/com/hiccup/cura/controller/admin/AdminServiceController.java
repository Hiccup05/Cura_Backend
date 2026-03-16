package com.hiccup.cura.controller.admin;

import com.hiccup.cura.dto.response.MedicalServiceResponseDto;
import com.hiccup.cura.service.medicalservice.MedicalServiceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("${api.prefix}/admin/service")
@RequiredArgsConstructor
public class AdminServiceController {
    private final MedicalServiceService serviceService;

    @GetMapping
    public ResponseEntity<List<MedicalServiceResponseDto>> getMedicalServices(){
        return ResponseEntity.ok(serviceService.getAll());
    }
}
