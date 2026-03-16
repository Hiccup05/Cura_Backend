package com.hiccup.cura.service.MedicalService;

import com.hiccup.cura.dto.response.MedicalServiceResponseDto;
import com.hiccup.cura.model.MedicalService;
import com.hiccup.cura.model.Specialization;
import com.hiccup.cura.repository.MedicalServiceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MedicalServiceService {
   private final MedicalServiceRepository medicalServiceRepository;

   public List<MedicalServiceResponseDto> getAll(){
       return medicalServiceRepository.findAll().stream().map(this::mapToDto).toList();
   }

   public List<MedicalServiceResponseDto> getActiveServices(){
       return medicalServiceRepository.findAllByIsActiveTrue().stream().map(this::mapToDto).toList();
   }

    public List<MedicalServiceResponseDto> getActiveSpecializationServices(Specialization specialization){
        return medicalServiceRepository.findAllBySpecializationAndIsActiveTrue(specialization).stream().map(this::mapToDto).toList();
    }

   private MedicalServiceResponseDto mapToDto(MedicalService service){
       return new MedicalServiceResponseDto(
               service.getName(), service.getPrice(), service.getDurationMinutes(),
                       service.getDescription(), service.getIsActive(),service.getSpecialization());
   }
}
