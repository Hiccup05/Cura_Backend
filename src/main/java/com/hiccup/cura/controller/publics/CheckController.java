package com.hiccup.cura.controller.publics;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("${api.prefix}/public/health")
@Tag(name="Health check", description = "Health Check of the Application")
public class CheckController {
    @Operation(summary = "Health check.")
    @GetMapping
    public String checkup(){
        return "Healthy website";
    }
}
