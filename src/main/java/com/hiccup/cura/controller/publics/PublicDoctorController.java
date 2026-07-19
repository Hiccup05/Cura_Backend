package com.hiccup.cura.controller.publics;

import com.hiccup.cura.dto.response.PublicDoctorResponseDto;
import com.hiccup.cura.dto.response.PublicScheduleResponseDto;
import com.hiccup.cura.service.doctor.DoctorScheduleService;
import com.hiccup.cura.service.doctor.DoctorService;
import com.hiccup.cura.service.SpecializationService;
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
public class PublicDoctorController {
    private final DoctorService doctorService;
    private final DoctorScheduleService doctorScheduleService;
    private final SpecializationService specializationService;

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

    @GetMapping("/{id}/schedule")
    public ResponseEntity<List<PublicScheduleResponseDto>> getDoctorScheduleByDoctorProfileId(@PathVariable Long id) {
        return ResponseEntity.ok(doctorScheduleService.getPublicSchedulesOfDoctor(id));
    }

    @GetMapping("/search")
    public Page<PublicDoctorResponseDto> getDoctors(@RequestParam(required = false) String name,
                                                    @RequestParam(defaultValue = "0") int page,
                                                    @RequestParam(defaultValue = "10") int size) {
        Pageable pageable=PageRequest.of(page, size);
        if (name == null || name.isBlank()) return doctorService.getPublicDoctors(pageable);
        return doctorService.searchByName(name, pageable);
    }
}
