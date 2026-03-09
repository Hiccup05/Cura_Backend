package com.hiccup.cura.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("${api.prefix}/checkup/check")
public class CheckController {
    public String checkup(){
        return "Healthy website";
    }
}
