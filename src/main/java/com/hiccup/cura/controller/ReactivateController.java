package com.hiccup.cura.controller;

import com.hiccup.cura.dto.request.ReactivationTokenRequestDto;
import com.hiccup.cura.exception.ErrorResponse;
import com.hiccup.cura.service.ReactivationTokenService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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

    @Operation(summary = "Email a reactivation link to a deactivated patient account.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Always returns the same generic message whether or not the account exists (prevents account enumeration); the email is only sent for an existing, deactivated account.")
    })
    @PostMapping("/initiate")
    public ResponseEntity<String> initiate(@RequestParam String email){
        return ResponseEntity.ok(reactivationTokenService.registerToken(email));
    }

    @Operation(summary = "Verify the emailed token and reactivate the account.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Account reactivated (also returned silently when the email does not match a deactivated account, to prevent enumeration)."),
            @ApiResponse(responseCode = "400", description = "Token not found, already used, or does not match the given email.",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "410", description = "Token expired (older than 24 hours); request a new link.",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping("/verify")
    public ResponseEntity<Void> verify(@RequestBody ReactivationTokenRequestDto tokenRequestDto){
        reactivationTokenService.reactivate(tokenRequestDto);
        return ResponseEntity.noContent().build();
    }
}
