package com.hiccup.cura.controller.admin;

import com.hiccup.cura.dto.reqeust.ChangeReceptionistRequestDto;
import com.hiccup.cura.dto.reqeust.ReceptionistRequestDto;
import com.hiccup.cura.dto.response.ReceptionistResponseDto;
import com.hiccup.cura.service.ReceptionistService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("${api.prefix}/admin/receptionists")
@RequiredArgsConstructor
public class AdminReceptionistController {
    private final ReceptionistService receptionistService;

    @GetMapping
    public ResponseEntity<List<ReceptionistResponseDto>> getReceptionists() {
        return ResponseEntity.ok(receptionistService.getReceptionists());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ReceptionistResponseDto> getReceptionist(@PathVariable Long id) {
        return ResponseEntity.ok(receptionistService.getReceptionist(id));
    }


}
