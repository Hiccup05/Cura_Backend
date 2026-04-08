package com.hiccup.cura.service.medicalservice;

import com.hiccup.cura.dto.reqeust.MedicalServiceRequestDto;
import com.hiccup.cura.dto.response.MedicalServiceResponseDto;
import com.hiccup.cura.dto.response.MessageResponseDto;
import com.hiccup.cura.enums.RoleType;
import com.hiccup.cura.exception.custom.DuplicateEntryException;
import com.hiccup.cura.exception.custom.ResourceNotFoundException;
import com.hiccup.cura.exception.custom.UnauthorizedUserAccessException;
import com.hiccup.cura.model.MedicalService;
import com.hiccup.cura.model.Specialization;
import com.hiccup.cura.model.User;
import com.hiccup.cura.repository.MedicalServiceRepository;
import com.hiccup.cura.repository.SpecializationRepository;
import com.hiccup.cura.repository.UserRepository;
import com.hiccup.cura.service.CloudinaryService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MedicalServiceService {
   private final MedicalServiceRepository medicalServiceRepository;
   private final SpecializationRepository specializationRepository;
   private final UserRepository userRepository;
   private final CloudinaryService cloudinaryService;

   public List<MedicalServiceResponseDto> getAll(){
       return medicalServiceRepository.findAll().stream().map(this::mapToDto).toList();
   }

   public MedicalServiceResponseDto getService(Long id){
       MedicalService medicalService=medicalServiceRepository.findById(id).orElseThrow(()->new ResourceNotFoundException("Service isn't created with id " + id));
       return mapToDto(medicalService);
   }

   public List<MedicalServiceResponseDto> getActiveServices(){
       return medicalServiceRepository.findAllByIsActiveTrue().stream().map(
               this::mapToDto
       ).toList();
   }

    public List<MedicalServiceResponseDto> getActiveSpecializationServices(Long specializationId){
        return medicalServiceRepository.findAllActiveServicesWithSpecialization(specializationId);
    }

    public List<MedicalServiceResponseDto> searchByName(String name){
       List<MedicalService> medicalService=medicalServiceRepository.searchByName(name);
       return medicalService.stream().map(this::mapToDto).toList();
    }

    @Transactional
    public MedicalServiceResponseDto createMedicalService(MedicalServiceRequestDto medicalServiceRequestDto){
       if(medicalServiceRepository.existsByNameAndSpecialization_id(medicalServiceRequestDto.getName(), medicalServiceRequestDto.getSpecializationId())){
           throw new DuplicateEntryException("Service already exists with this name under this specialization");
       }
        Specialization specialization=specializationRepository.findById(medicalServiceRequestDto.getSpecializationId())
                .orElseThrow(()-> new ResourceNotFoundException("Doctor specialization isn't created with id "+medicalServiceRequestDto.getSpecializationId()));
       MedicalService medicalService=MedicalService.builder().
               name(medicalServiceRequestDto.getName())
               .description(medicalServiceRequestDto.getDescription())
               .price(medicalServiceRequestDto.getPrice())
               .durationMinutes(medicalServiceRequestDto.getDurationMinutes())
               .isActive(true)
               .specialization(specialization)
               .build();
       return mapToDto(medicalServiceRepository.save(medicalService));
    }

    @Transactional
    public MedicalServiceResponseDto updateMedicalService(Long id, MedicalServiceRequestDto medicalServiceRequestDto){
        MedicalService medicalService = medicalServiceRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Service isn't created with id " + id));
        if(medicalServiceRequestDto.getName()!=null){
            medicalService.setName(medicalServiceRequestDto.getName());
        }
        if(medicalServiceRequestDto.getPrice()!=null){
            medicalService.setPrice(medicalServiceRequestDto.getPrice());
        }
        if(medicalServiceRequestDto.getDurationMinutes()!=null){
            medicalService.setDurationMinutes(medicalServiceRequestDto.getDurationMinutes());
        }
        if(medicalServiceRequestDto.getDescription()!=null){
            medicalService.setDescription(medicalServiceRequestDto.getDescription());
        }
        if(medicalServiceRequestDto.getSpecializationId()!=null){
            Specialization specialization=specializationRepository.findById(medicalServiceRequestDto.getSpecializationId()).orElseThrow(()-> new ResourceNotFoundException("Specialization cannot be found with id "+medicalServiceRequestDto.getSpecializationId()));
            medicalService.setSpecialization(specialization);
        }
        return mapToDto(medicalServiceRepository.save(medicalService));
    }

    public MessageResponseDto toggleStatus(Long id) {
        MedicalService service = medicalServiceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Medical service not found with id " + id));
        service.setIsActive(!service.getIsActive());
        medicalServiceRepository.save(service);
        return new MessageResponseDto("Service " + (service.getIsActive() ? "activated" : "deactivated") + " successfully", LocalDateTime.now());
    }

    public String uploadPhoto(Long userId, Long id, MultipartFile file) throws IOException {
        MedicalService service = medicalServiceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Medical service not found with id " + id));
        User user=userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("User not found with id " + userId));
        if(user.getRole().stream().noneMatch(role->role.getName().equals(RoleType.ADMIN))){
            throw new UnauthorizedUserAccessException("The user is not a admin.");
        }
        String publicId="service_"+service.getId();
        String photoUrl = cloudinaryService.uploadServicePhoto(file, publicId);
        service.setPhotoUrl(photoUrl);
        return photoUrl;
    }

   private MedicalServiceResponseDto mapToDto(MedicalService service){
       return new MedicalServiceResponseDto(
               service.getId(), service.getName(), service.getPrice(), service.getDurationMinutes(),
                       service.getDescription(), service.getIsActive(),service.getSpecialization().getId(), service.getSpecialization().getName());
   }
}
