package com.hiccup.cura.controller.publics;

import com.hiccup.cura.service.ReceptionistService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("${api.prefix}/receptionist")
public class ReceptionistController {
    private final ReceptionistService receptionistService;
}
