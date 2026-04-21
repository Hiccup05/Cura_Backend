package com.hiccup.cura.controller.admin;

import com.hiccup.cura.dto.response.AdminProfileDto;
import com.hiccup.cura.dto.response.AdminStatsResponseDto;
import com.hiccup.cura.security.CustomUser;
import com.hiccup.cura.service.admin.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("${api.prefix}/admin")
public class AdminController {
    private final AdminService adminService;

    @GetMapping("/profile")
    public ResponseEntity<AdminProfileDto> getAdminProfile(@AuthenticationPrincipal CustomUser user) {
        return ResponseEntity.ok(adminService.getAdminProfile(user.getId()));
    }

    @GetMapping("/stats")
    public ResponseEntity<AdminStatsResponseDto> getStats() {
        return ResponseEntity.ok(adminService.getStats());
    }
}
