package com.hiccup.cura.controller.admin;

import com.hiccup.cura.dto.response.PatientResponseDto;
import com.hiccup.cura.service.PatientService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RequiredArgsConstructor
@RestController
@RequestMapping("${api.prefix}/admin/patients")
@Tag(name="Admin Patient", description = "Admin control over User with role Patient")
public class AdminPatientController {
    private final PatientService patientService;

    @Operation(summary = "List patients, paginated.")
    @GetMapping
    public ResponseEntity<Page<PatientResponseDto>> getPatients(@RequestParam(defaultValue = "0") int page,
                                                                @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(patientService.getAll(pageable));
    }

    @Operation(summary = " Read a patient.")
    @GetMapping("/{id}")
    public ResponseEntity<PatientResponseDto> getPatientById(@PathVariable Long id) {
        return ResponseEntity.ok(patientService.getById(id));
    }

    @Operation(summary = " Delete a patient.")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePatientById(@PathVariable Long id) {
        patientService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
