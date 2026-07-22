package com.hiccup.cura.controller;

import com.hiccup.cura.dto.request.ReactivationTokenRequestDto;
import com.hiccup.cura.service.ReactivationTokenService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("${api.prefix}/reactivate")
@Tag(name="Reactivation", description = "Reactivation of user with role patient.")
public class ReactivateController {

    private final ReactivationTokenService reactivationTokenService;

    @PostMapping("/initiate")
    public ResponseEntity<String> initiate(@RequestParam String email){
        return ResponseEntity.ok(reactivationTokenService.registerToken(email));
    }

    @PostMapping("/verify")
    public ResponseEntity<Void> verify(@RequestBody ReactivationTokenRequestDto tokenRequestDto){
        reactivationTokenService.reactivate(tokenRequestDto);
        return ResponseEntity.noContent().build();
    }
}
