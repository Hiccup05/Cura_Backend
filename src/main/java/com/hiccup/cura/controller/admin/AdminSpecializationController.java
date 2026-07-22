package com.hiccup.cura.controller.admin;

import com.hiccup.cura.dto.request.SpecializationRequestDto;
import com.hiccup.cura.dto.response.SpecializationDto;
import com.hiccup.cura.service.SpecializationService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("${api.prefix}/admin/specializations")
@RequiredArgsConstructor
@Tag(name="Admin Specialization", description = "Admin action on Specializations")
public class AdminSpecializationController {
    private final SpecializationService service;

    @GetMapping
    public ResponseEntity<List<SpecializationDto>> getAll(){
        return ResponseEntity.ok(service.getAll());
    }

    @PostMapping
    public ResponseEntity<SpecializationDto> createSpecialization(@Valid @RequestBody  SpecializationRequestDto specializationRequestDto){
        SpecializationDto created =service.create(specializationRequestDto);
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(created.getId())
                .toUri();
        return ResponseEntity.created(location).body(created);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSpecialization(@PathVariable Long id){
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
