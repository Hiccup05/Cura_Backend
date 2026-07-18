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
import com.hiccup.cura.mapper.MedicalServiceMapper;
import com.hiccup.cura.repository.MedicalServiceRepository;
import com.hiccup.cura.repository.SpecializationRepository;
import com.hiccup.cura.repository.UserRepository;
import com.hiccup.cura.service.CloudinaryService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
   private final MedicalServiceMapper medicalServiceMapper;

   public Page<MedicalServiceResponseDto> getAll(Pageable pageable){
       Page<MedicalService> allService = medicalServiceRepository.findAll(pageable);
       return allService.map(medicalServiceMapper::toDto);
   }

   @Cacheable(value="medical service", key="#id")
   public MedicalServiceResponseDto getService(Long id){
       MedicalService medicalService=medicalServiceRepository.findById(id).orElseThrow(()->new ResourceNotFoundException("Service isn't created with id " + id));
       return medicalServiceMapper.toDto(medicalService);
   }

   //use to fetch raw medical service data internally for other services
    public MedicalService getServiceInternal(Long id){
        return medicalServiceRepository.findById(id).orElseThrow(()->new ResourceNotFoundException("Service isn't created with id " + id));

    }

   public Page<MedicalServiceResponseDto> getActiveServices(Pageable pageable){
       Page<MedicalService> allByIsActiveTrue = medicalServiceRepository.findAllByIsActiveTrue(pageable);
       return allByIsActiveTrue.map(medicalServiceMapper::toDto);
   }

    public List<MedicalServiceResponseDto> getActiveSpecializationServices(Long specializationId){
        return medicalServiceRepository.findAllActiveServicesWithSpecialization(specializationId);
    }

    public Page<MedicalServiceResponseDto> searchByKeyword(String name, Pageable pageable){
        Page<MedicalService> medicalServices = medicalServiceRepository.searchByKeyword(name, pageable);
        return medicalServices.map(medicalServiceMapper::toDto);
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
       return medicalServiceMapper.toDto(medicalServiceRepository.save(medicalService));
    }

    @CacheEvict(value="medical service", key="#id")
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
        return medicalServiceMapper.toDto(medicalServiceRepository.save(medicalService));
    }

    @CacheEvict(value="medical service", key = "#id")
    public MessageResponseDto toggleStatus(Long id) {
        MedicalService service = medicalServiceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Medical service not found with id " + id));
        service.setIsActive(!service.getIsActive());
        medicalServiceRepository.save(service);
        return new MessageResponseDto("Service " + (service.getIsActive() ? "activated" : "deactivated") + " successfully", LocalDateTime.now());
    }

    @CacheEvict(value="medical service photo", key="#id")
    public String uploadPhoto(Long id, MultipartFile file) throws IOException {
        MedicalService service = medicalServiceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Medical service not found with id " + id));
        String publicId="service_"+service.getId();
        String photoUrl = cloudinaryService.uploadServicePhoto(file, publicId);
        service.setPhotoUrl(photoUrl);
        medicalServiceRepository.save(service);
        return photoUrl;
    }

    @Cacheable(value="medical service photo", key="#id")
    public String getPhoto(Long id){
        MedicalService service = medicalServiceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Medical service not found with id " + id));
        return service.getPhotoUrl()!=null?service.getPhotoUrl():"";
    }

    @CacheEvict(value="medical service photo", key="#id")
    public void deletePhoto(Long id) throws IOException {
        MedicalService service = medicalServiceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Medical service not found with id " + id));
        String publicId = extractPublicId(service.getPhotoUrl());
        cloudinaryService.deleteServicePhoto(publicId);
        service.setPhotoUrl(null);
        medicalServiceRepository.save(service);
    }

    private String extractPublicId(String url) {
        // URL format: https://res.cloudinary.com/cloud/image/upload/v123456/cura/users/user_1.jpg
        // We need: cura/users/user_1
        String withoutExtension = url.substring(0, url.lastIndexOf('.'));
        String afterUpload = withoutExtension.substring(withoutExtension.indexOf("upload/") + 7);
        // strip version segment if present (starts with 'v' followed by digits)
        return afterUpload.replaceFirst("^v\\d+/", "");
    }
}
