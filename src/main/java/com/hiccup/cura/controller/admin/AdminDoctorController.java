package com.hiccup.cura.controller.admin;

import com.hiccup.cura.dto.reqeust.ChangeStatusRequestDto;
import com.hiccup.cura.dto.reqeust.DoctorRequestDto;
import com.hiccup.cura.dto.reqeust.ScheduleRequestDto;
import com.hiccup.cura.dto.reqeust.ScheduleUpdateRequestDto;
import com.hiccup.cura.dto.response.DoctorDto;
import com.hiccup.cura.dto.response.MessageResponseDto;
import com.hiccup.cura.dto.response.ScheduleResponseDto;
import com.hiccup.cura.service.DoctorScheduleService;
import com.hiccup.cura.service.doctor.DoctorService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("${api.prefix}/admin/doctors")
@RequiredArgsConstructor
public class AdminDoctorController {
    private final DoctorService doctorService;
    private final DoctorScheduleService scheduleService;

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

    @PostMapping("/{id}/schedules")
    public ResponseEntity<ScheduleResponseDto> createSchedule(@PathVariable Long id, @RequestBody ScheduleRequestDto scheduleRequestDto){
        ScheduleResponseDto created = scheduleService.createSchedule(scheduleRequestDto, id);
        URI location=ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{doctorId}/schedule")
                .buildAndExpand(created.getId())
                .toUri();
        return ResponseEntity.created(location).body(created);
    }

    @PatchMapping("/{id}/schedules/{scheduleId}")
    public ResponseEntity<ScheduleResponseDto> updateScheduleOfDoctor(@PathVariable Long id, @PathVariable Long scheduleId, @RequestBody ScheduleUpdateRequestDto scheduleUpdateRequestDto){
        return ResponseEntity.ok(scheduleService.updateScheduleOfDoctor(scheduleUpdateRequestDto, id, scheduleId));
    }

    @PatchMapping("/{id}/schedules/{scheduleId}/toggle")
    public ResponseEntity<ScheduleResponseDto> toggleScheduleStatus(@PathVariable Long id, @PathVariable Long scheduleId){
        return ResponseEntity.ok(scheduleService.toggleScheduleOfDoctor(id, scheduleId));
    }

    @DeleteMapping("/{id}/schedules/{scheduleId}")
    public ResponseEntity<MessageResponseDto> deleteSchedule(@PathVariable Long id,  @PathVariable Long scheduleId){
        return ResponseEntity.ok(scheduleService.deleteDoctorSchedule(id, scheduleId));
    }

}
