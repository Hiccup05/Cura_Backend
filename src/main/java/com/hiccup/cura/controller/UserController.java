package com.hiccup.cura.controller;

import com.hiccup.cura.dto.response.ApiResposne;
import com.hiccup.cura.security.CustomUser;
import com.hiccup.cura.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("${api.prefix}/user")
@RequiredArgsConstructor
public class UserController {
    private UserService userService;

    @PostMapping("/profile/picture")
    public ResponseEntity<ApiResposne> uploadProfilePicture(@RequestParam MultipartFile file, @AuthenticationPrincipal CustomUser user) throws IOException {

        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            return ResponseEntity.badRequest().body( new ApiResposne("Only image files are allowed", null) );
        }
        if (file.getSize() > 2 * 1024 * 1024) {
            return ResponseEntity.badRequest().body(new ApiResposne("File size must be under 2MB", null));
        }
        return ResponseEntity.ok(new ApiResposne("Upload Successful", userService.updateProfilePictureUrl(user.getId(), file)));
    }
}
