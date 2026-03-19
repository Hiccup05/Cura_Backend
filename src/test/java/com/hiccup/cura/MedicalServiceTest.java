package com.hiccup.cura;

import com.hiccup.cura.dto.response.MedicalServiceResponseDto;
import com.hiccup.cura.service.medicalservice.MedicalServiceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

@SpringBootTest
@RequiredArgsConstructor
@Slf4j
@ActiveProfiles("test")
public class MedicalServiceTest {
    @Autowired
    MedicalServiceService service;

    @Test
    public void testMedicalService(){
        List<MedicalServiceResponseDto> all = service.getAll();

        log.info(all.get(0).toString());
    }
}
