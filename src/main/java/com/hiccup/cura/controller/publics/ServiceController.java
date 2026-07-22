package com.hiccup.cura.controller.publics;

import com.hiccup.cura.dto.response.MedicalServiceResponseDto;
import com.hiccup.cura.service.MedicalServiceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("${api.prefix}/public/services")
@RequiredArgsConstructor
@Tag(name="Services", description = "Clinic Services")
public class ServiceController {
    private final MedicalServiceService medicalService;

    @Operation(summary = " Browse active services (+ /search?keyword=, /{id}/photo).")
    @GetMapping
    public ResponseEntity<Page<MedicalServiceResponseDto>> getAllServices(@RequestParam(defaultValue = "0") int page,
                                                                          @RequestParam(defaultValue = "10") int size){
        Pageable pageable=PageRequest.of(page, size);
        return ResponseEntity.ok(medicalService.getActiveServices(pageable));
    }

    @GetMapping("/search")
    public ResponseEntity<Page<MedicalServiceResponseDto>> searchByKeyword(@RequestParam String keyword,
                                                                        @RequestParam(defaultValue = "0") int page,
                                                                        @RequestParam(defaultValue = "10") int size){
        Pageable pageable=PageRequest.of(page, size);
        return ResponseEntity.ok(medicalService.searchByKeyword(keyword, pageable));
    }

    @GetMapping("/{id}/photo")
    public ResponseEntity<String> getServicePhoto(@PathVariable Long id){
        return ResponseEntity.ok(medicalService.getPhoto(id));
    }
}
