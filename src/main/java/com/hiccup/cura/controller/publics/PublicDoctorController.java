package com.hiccup.cura.controller.publics;

import com.hiccup.cura.dto.response.PublicDoctorResponseDto;
import com.hiccup.cura.dto.response.PublicScheduleResponseDto;
import com.hiccup.cura.service.doctor.DoctorScheduleService;
import com.hiccup.cura.service.doctor.DoctorService;
import com.hiccup.cura.service.SpecializationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("${api.prefix}/public/doctors")
@RequiredArgsConstructor
@Tag(name="Public Doctor", description = "Information available to public about doctors")
public class PublicDoctorController {
    private final DoctorService doctorService;
    private final DoctorScheduleService doctorScheduleService;

    @Operation(summary = "Browse active doctors, paginated (+ /{id} detail, /{id}/schedules availability, /search?name=).")
    @GetMapping
    public ResponseEntity<Page<PublicDoctorResponseDto>> getPublicDoctors(@RequestParam(defaultValue = "0") int page,
                                                                          @RequestParam(defaultValue = "10") int size){
        Pageable pageable= PageRequest.of(page, size);
        return  ResponseEntity.ok(doctorService.getPublicDoctors(pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<PublicDoctorResponseDto> getPublicDoctor(@PathVariable Long id){
        return ResponseEntity.ok(doctorService.getPublicDoctor(id));
    }

    @GetMapping("/{id}/schedules")
    public ResponseEntity<List<PublicScheduleResponseDto>> getDoctorScheduleByDoctorProfileId(@PathVariable Long id) {
        return ResponseEntity.ok(doctorScheduleService.getPublicSchedulesOfDoctor(id));
    }

    @GetMapping("/search")
    public ResponseEntity<Page<PublicDoctorResponseDto>> getDoctors(@RequestParam(required = false) String name,
                                                    @RequestParam(defaultValue = "0") int page,
                                                    @RequestParam(defaultValue = "10") int size) {
        Pageable pageable=PageRequest.of(page, size);
        if (name == null || name.isBlank()) return ResponseEntity.ok(doctorService.getPublicDoctors(pageable));
        return ResponseEntity.ok(doctorService.searchByName(name, pageable));
    }
}
