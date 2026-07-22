package com.hiccup.cura.controller.admin;

import com.hiccup.cura.dto.request.ChangeReceptionistRequestDto;
import com.hiccup.cura.dto.request.ReceptionistRequestDto;
import com.hiccup.cura.dto.response.ReceptionistResponseDto;
import com.hiccup.cura.service.ReceptionistService;
import io.swagger.v3.oas.annotations.Operation;
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

@RestController
@RequestMapping("${api.prefix}/admin/receptionists")
@RequiredArgsConstructor
@Tag(name="Admin Receptionist", description = "Admin control on User with role Receptionist")
public class AdminReceptionistController {
    private final ReceptionistService receptionistService;

    @Operation(summary = "List receptionists, paginated.")
    @GetMapping
    public ResponseEntity<Page<ReceptionistResponseDto>> getReceptionists(@RequestParam(defaultValue = "0") int page,
                                                                          @RequestParam(defaultValue = "10") int size) {
        Pageable pageable= PageRequest.of(page,size );
        return ResponseEntity.ok(receptionistService.getReceptionists(pageable));
    }

    @Operation(summary = " Read a receptionist")
    @GetMapping("/{id}")
    public ResponseEntity<ReceptionistResponseDto> getReceptionist(@PathVariable Long id) {
        return ResponseEntity.ok(receptionistService.getReceptionist(id));
    }

    @Operation(summary = "Promote a user (by userId in body) to receptionist (validated).")
    @PostMapping
    public ResponseEntity<ReceptionistResponseDto> createReceptionist(@Valid @RequestBody ReceptionistRequestDto requestDto) {
        ReceptionistResponseDto created = receptionistService.createReceptionist(requestDto.getUserId(), requestDto);
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(created.getId())
                .toUri();
        return ResponseEntity.created(location).body(created);
    }

    @Operation(summary = "Update a receptionist")
    @PatchMapping("/{id}")
    public ResponseEntity<ReceptionistResponseDto> updateReceptionist(@PathVariable Long id, @RequestBody ReceptionistRequestDto requestDto) {
        return ResponseEntity.ok(receptionistService.updateReceptionist(id, requestDto));
    }

    @Operation(summary = "Change the active status")
    @PatchMapping("/{id}/status")
    public ResponseEntity<ReceptionistResponseDto> changeStatus(@PathVariable Long id, @RequestBody ChangeReceptionistRequestDto changeReceptionistRequestDto) {
        return ResponseEntity.ok(receptionistService.changeStatus(id, changeReceptionistRequestDto));
    }

    @Operation(summary = "Delete a receptionist")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteReceptionist(@PathVariable Long id) {
        receptionistService.deleteReceptionist(id);
        return ResponseEntity.noContent().build();
    }
}
