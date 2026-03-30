package com.hiccup.cura.controller;

import com.hiccup.cura.dto.response.ReceptionistResponseDto;
import com.hiccup.cura.security.CustomUser;
import com.hiccup.cura.service.ReceptionistService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("${api.prefix}/receptionist")
public class ReceptionistController {
    private final ReceptionistService receptionistService;

    @GetMapping
    public ResponseEntity<ReceptionistResponseDto> getReceptionist(@AuthenticationPrincipal CustomUser user) {
        return ResponseEntity.ok(receptionistService.getReceptionist(user.getId()));
    }
}
