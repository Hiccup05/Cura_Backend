package com.hiccup.cura.service;

import com.hiccup.cura.dto.reqeust.PrescriptionRequestDto;
import com.hiccup.cura.dto.response.PrescriptionResponseDto;
import com.hiccup.cura.exception.custom.ResourceNotFoundException;
import com.hiccup.cura.exception.custom.UnauthorizedUserAccessException;
import com.hiccup.cura.model.Appointment;
import com.hiccup.cura.model.Prescription;
import com.hiccup.cura.repository.AppointmentRepository;
import com.hiccup.cura.repository.PrescriptionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PrescriptionService {
    private final PrescriptionRepository prescriptionRepository;
    private final AppointmentRepository appointmentRepository;

    public PrescriptionResponseDto updatePrescription(PrescriptionRequestDto prescription,Long prescriptionId, Long doctorId) {
        Appointment appointment = appointmentRepository.findById(prescriptionId).orElseThrow(() -> new ResourceNotFoundException("Appointment Not Found with id " + prescriptionId));
        if(!appointment.getDoctor().getId().equals(doctorId)){
            throw new UnauthorizedUserAccessException("Doctor Id dont match with the appointment id whose prescription is being updated");
        }
        Prescription saved = prescriptionRepository.findById(prescriptionId).orElseThrow(() -> new ResourceNotFoundException("Prescription not found with id " + prescriptionId));
        if(prescription.getDescription()!=null && !prescription.getDescription().isBlank()){
            saved.setDescription(prescription.getDescription());
        }
        return  mapToDto(prescriptionRepository.save(saved));
    }

    private PrescriptionResponseDto mapToDto(Prescription prescription){
        return new  PrescriptionResponseDto(prescription.getId(), prescription.getDescription());
    }
}
