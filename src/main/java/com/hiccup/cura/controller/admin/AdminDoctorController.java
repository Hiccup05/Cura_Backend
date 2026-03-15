package com.hiccup.cura.controller.admin;

import com.hiccup.cura.dto.reqeust.ChangeStatusRequestDto;
import com.hiccup.cura.dto.reqeust.DoctorRequestDto;
import com.hiccup.cura.dto.response.DoctorDto;
import com.hiccup.cura.dto.response.MessageResponseDto;
import com.hiccup.cura.enums.DoctorStatus;
import com.hiccup.cura.service.doctor.DoctorService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.print.Doc;
import java.net.URI;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("${api.prefix}/admin/doctors")
@RequiredArgsConstructor
public class AdminDoctorController {
    private final DoctorService doctorService;

    @GetMapping
    public ResponseEntity<List<DoctorDto>> getDoctors(){
        return ResponseEntity.ok(doctorService.getDoctors());
    }

    @GetMapping("/doctor/{id}")
    public ResponseEntity<DoctorDto> getDoctor(@PathVariable Long id){
        return ResponseEntity.ok(doctorService.getDoctor(id));
    }

    @PostMapping("/{id}")
    public ResponseEntity<DoctorDto> createDoctor(@PathVariable Long id, @RequestBody DoctorRequestDto doctorRequestDto){
        DoctorDto created = doctorService.createDoctor(id, doctorRequestDto);
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(created.getId())
                .toUri();
        return ResponseEntity.created(location).body(created);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<DoctorDto> updateDoctor(@PathVariable Long id, @RequestBody DoctorRequestDto doctorRequestDto){
        return ResponseEntity.ok(doctorService.updateDoctor(id,doctorRequestDto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<MessageResponseDto> deleteDoctor(@PathVariable Long id){
        return ResponseEntity.ok(doctorService.deleteDoctor(id));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<DoctorDto> changeStatus(@PathVariable Long id, @RequestBody ChangeStatusRequestDto changeStatusRequestDto){
        return ResponseEntity.ok(doctorService.changeStatus(id, changeStatusRequestDto));
    }
}
