package com.hiccup.cura.controller.receptionist;

import com.hiccup.cura.dto.request.ReceptionistRequestDto;
import com.hiccup.cura.dto.response.ReceptionistResponseDto;
import com.hiccup.cura.security.CustomUser;
import com.hiccup.cura.service.ReceptionistService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("${api.prefix}/receptionists")
@Tag(name="Receptionists", description = "Fetch, Update")
public class ReceptionistController {
    private final ReceptionistService receptionistService;

    @Operation(summary = "Get my receptionist profile.")
    @GetMapping
    public ResponseEntity<ReceptionistResponseDto> getReceptionist(@AuthenticationPrincipal CustomUser user) {
        return ResponseEntity.ok(receptionistService.getReceptionist(user.getId()));
    }

    @Operation(summary = " Update my receptionist profile.")
    @PatchMapping
    public ResponseEntity<ReceptionistResponseDto> updateReceptionist(@RequestBody ReceptionistRequestDto requestDto, @AuthenticationPrincipal CustomUser user) {
        return ResponseEntity.ok(receptionistService.updateReceptionist(user.getId(), requestDto));
    }

}
