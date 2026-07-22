package com.hiccup.cura.controller.admin;

import com.hiccup.cura.dto.request.*;
import com.hiccup.cura.dto.response.*;
import com.hiccup.cura.service.doctor.DoctorScheduleService;
import com.hiccup.cura.service.doctor.DoctorService;
import com.hiccup.cura.service.doctor.LeaveService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("${api.prefix}/admin/doctors")
@RequiredArgsConstructor
@Tag(name="Admin Doctor", description = "Admin control on User with role Doctor")
public class AdminDoctorController {
    private final DoctorService doctorService;
    private final DoctorScheduleService scheduleService;
    private final LeaveService leaveService;

    @GetMapping
    public ResponseEntity<Page<DoctorDto>> getDoctors(@RequestParam(defaultValue = "0") int page,
                                                      @RequestParam(defaultValue = "10") int size){
        Pageable pageable= PageRequest.of(page, size);
        return ResponseEntity.ok(doctorService.getDoctors(pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<DoctorDto> getDoctor(@PathVariable Long id){
        return ResponseEntity.ok(doctorService.getDoctor(id));
    }

    @PostMapping
    public ResponseEntity<DoctorDto> createDoctor(@Valid @RequestBody DoctorRequestDto doctorRequestDto){
        DoctorDto created = doctorService.createDoctor(doctorRequestDto.getUserId(), doctorRequestDto);
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
    public ResponseEntity<Void> deleteDoctor(@PathVariable Long id){
        doctorService.deleteDoctor(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<DoctorDto> changeStatus(@PathVariable Long id, @RequestBody ChangeDoctorStatusRequestDto changeDoctorStatusRequestDto){
        return ResponseEntity.ok(doctorService.changeStatus(id, changeDoctorStatusRequestDto));
    }

    @GetMapping("/{id}/schedules")
    public ResponseEntity<List<ScheduleResponseDto>> getDoctorScheduleByDoctorProfileId(@PathVariable Long id) {
        return ResponseEntity.ok(scheduleService.getSchedulesOfDoctor(id));
    }

    @GetMapping("/{id}/schedules/{scheduleId}")
    public ResponseEntity<ScheduleResponseDto> getDoctorSchedule(@PathVariable Long id, @PathVariable Long scheduleId) {
        return ResponseEntity.ok(scheduleService.getScheduleOfDoctor(id, scheduleId));
    }

    @PostMapping("/{id}/schedules")
    public ResponseEntity<ScheduleResponseDto> createSchedule(@PathVariable Long id, @RequestBody ScheduleRequestDto scheduleRequestDto){
        ScheduleResponseDto created = scheduleService.createSchedule(scheduleRequestDto, id);
        URI location=ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{scheduleId}")
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
    public ResponseEntity<Void> deleteSchedule(@PathVariable Long id,  @PathVariable Long scheduleId){
        scheduleService.deleteDoctorSchedule(id,  scheduleId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/leave")
    public ResponseEntity<List<LeaveResponseDto>> getLeaveById(@PathVariable Long id){
        return ResponseEntity.ok(leaveService.getLeaves(id));
    }

    @GetMapping("/{id}/leave/{leaveId}")
    public ResponseEntity<LeaveResponseDto> getLeaveById(@PathVariable Long id, @PathVariable Long leaveId){
        return ResponseEntity.ok(leaveService.getLeave(id, leaveId));
    }


    @PostMapping("/{id}/leave")
    public ResponseEntity<LeaveResponseDto> leaveDoctor(@PathVariable Long id, @RequestBody LeaveRequestDto leaveRequestDto){
        LeaveResponseDto created=leaveService.createLeave(id,leaveRequestDto);
        URI location=ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{leaveId}")
                .buildAndExpand(created.getId())
                .toUri();
        return ResponseEntity.created(location).body(created);
    }

    @DeleteMapping("/{id}/leave/{leaveId}")
    public ResponseEntity<Void>  deleteLeave(@PathVariable Long id, @PathVariable Long leaveId){
        leaveService.deleteLeave(id, leaveId);
        return ResponseEntity.noContent().build();
    }

}
