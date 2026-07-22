package com.hiccup.cura.controller;

import com.hiccup.cura.dto.response.ApiResponse;
import com.hiccup.cura.security.CustomUser;
import com.hiccup.cura.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("${api.prefix}/users")
@RequiredArgsConstructor
@Tag(name="User", description = "Toggle status, upload profile, delete profile")
public class UserController {
    private final UserService userService;

    @Operation(summary = "Deactivate/reactivate my own account (PATIENT only).")
    @PatchMapping("/toggle")
    public ResponseEntity<Void> toggleStatus(@AuthenticationPrincipal CustomUser user){
        userService.toggleStatus(user.getId());
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = " Upload a profile picture (image, ≤2 MB).")
    @PostMapping("/profile/picture")
    public ResponseEntity<ApiResponse> uploadProfilePicture(@RequestParam MultipartFile file, @AuthenticationPrincipal CustomUser user) throws IOException {

        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            return ResponseEntity.badRequest().body( new ApiResponse("Only image files are allowed", null) );
        }
        if (file.getSize() > 2 * 1024 * 1024) {
            return ResponseEntity.badRequest().body(new ApiResponse("File size must be under 2MB", null));
        }
        return ResponseEntity.ok(new ApiResponse("Upload Successful", userService.updateProfilePictureUrl(user.getId(), file)));
    }

    @Operation(summary = "Remove my profile picture.")
    @DeleteMapping("/profile/picture")
    public ResponseEntity<Void> deleteProfilePicture(@AuthenticationPrincipal CustomUser user) throws IOException {
        userService.deleteProfilePicture(user.getId());
        return ResponseEntity.noContent().build();
    }
}
