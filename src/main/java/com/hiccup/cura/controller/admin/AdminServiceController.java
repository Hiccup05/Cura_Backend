package com.hiccup.cura.controller.admin;

import com.hiccup.cura.dto.reqeust.MedicalServiceRequestDto;
import com.hiccup.cura.dto.response.MedicalServiceResponseDto;
import com.hiccup.cura.dto.response.MessageResponseDto;
import com.hiccup.cura.security.CustomUser;
import com.hiccup.cura.service.medicalservice.MedicalServiceService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.IOException;
import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("${api.prefix}/admin/services")
@RequiredArgsConstructor
public class AdminServiceController {
    private final MedicalServiceService serviceService;

    @GetMapping
    public ResponseEntity<Page<MedicalServiceResponseDto>> getMedicalServices(@RequestParam(defaultValue = "0") int page,
                                                                              @RequestParam(defaultValue = "10") int size){
        Pageable pageable=PageRequest.of(page, size);
        return ResponseEntity.ok(serviceService.getAll(pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<MedicalServiceResponseDto> getMedicalService(@PathVariable Long id){
        return ResponseEntity.ok(serviceService.getService(id));
    }

    @PostMapping
    public ResponseEntity<MedicalServiceResponseDto> createMedicalService(@RequestBody MedicalServiceRequestDto medicalServiceRequestDto){
        MedicalServiceResponseDto created=serviceService.createMedicalService(medicalServiceRequestDto);
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(created.getId())
                .toUri();
        return ResponseEntity.created(location).body(created);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<MedicalServiceResponseDto> updateMedicalService(@PathVariable Long id, @RequestBody MedicalServiceRequestDto medicalServiceRequestDto){
        return ResponseEntity.ok(serviceService.updateMedicalService(id, medicalServiceRequestDto));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<MessageResponseDto> toggleStatus(@PathVariable Long id){
        return ResponseEntity.ok(serviceService.toggleStatus(id));
    }

    @PostMapping("/{id}/photo")
    public ResponseEntity<String> uploadPhoto(@PathVariable Long id, @RequestParam("file") MultipartFile file, @AuthenticationPrincipal CustomUser user) throws IOException {
        return ResponseEntity.ok(serviceService.uploadPhoto(id, file));
    }

    @DeleteMapping("/{id}/photo")
    public ResponseEntity<Void> deleteServicePhoto(@PathVariable Long id) throws IOException {
        serviceService.deletePhoto(id);
        return ResponseEntity.noContent().build();
    }
}
