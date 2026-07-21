package com.hiccup.cura.controller;

import com.hiccup.cura.model.Specialization;
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
@RequestMapping("${api.prefix}/specialization")
@RequiredArgsConstructor
@Tag(name="Specialization", description = "About the specialization that doctors in CURA have.")
public class SpecializationController {
    private final SpecializationService service;

    @GetMapping
    public ResponseEntity<List<Specialization>> getAll(){
        return ResponseEntity.ok(service.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Specialization> getOne(@PathVariable Long id){
        return ResponseEntity.ok(service.getById(id));
    }
}
