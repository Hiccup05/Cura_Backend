package com.hiccup.cura.mapper;

import com.hiccup.cura.dto.response.AppointmentResponseDto;
import com.hiccup.cura.dto.response.AppointmentSummaryDto;
import com.hiccup.cura.model.Appointment;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AppointmentMapper {
    private final PrescriptionMapper prescriptionMapper;

    public AppointmentResponseDto toDto(Appointment appointment) {
        return AppointmentResponseDto.builder()
                .id(appointment.getId())
                .doctorId(appointment.getDoctor().getId())
                .medicalServiceId(appointment.getMedicalService().getId())
                .medicalServiceName(appointment.getMedicalService().getName())
                .appointmentDate(appointment.getAppointmentDate())
                .appointmentTime(appointment.getAppointmentTime())
                .status(appointment.getStatus())
                .type(appointment.getType())
                .reason(appointment.getReason())
                .price(appointment.getMedicalService().getPrice())
                .durationMinutes(appointment.getMedicalService().getDurationMinutes())
                .isPaid(appointment.getIsPaid())
                .paymentMethod(appointment.getPaymentMethod())
                .bookedAt(appointment.getBookedAt())
                .patientId(appointment.getPatient() != null ? appointment.getPatient().getId() : null)
                .patientName(appointment.getPatient() != null ?
                        appointment.getPatient().getFirstName() + " " + appointment.getPatient().getLastName() : null)
                .receptionistId(appointment.getReceptionist() != null ? appointment.getReceptionist().getId() : null)
                .receptionistName(appointment.getReceptionist() != null ?
                        appointment.getReceptionist().getFirstName() + " " + appointment.getReceptionist().getLastName() : null)
                .walkInPatientName(appointment.getWalkInPatientName())
                .walkInPatientPhone(appointment.getWalkInPatientPhone())
                .prescriptionResponseDto(appointment.getPrescription() != null ? prescriptionMapper.toDto(appointment.getPrescription()) : null)
                .build();
    }

    public AppointmentSummaryDto toSummaryDto(Appointment appointment) {
        return AppointmentSummaryDto.builder()
                .appointmentId(appointment != null ? appointment.getId() : null)
                .appointmentDate(appointment != null ? appointment.getAppointmentDate() : null)
                .appointmentTime(appointment != null ? appointment.getAppointmentTime() : null)
                .appointmentStatus(appointment != null ? appointment.getStatus() : null)
                .doctorId(
                        appointment != null && appointment.getDoctor() != null
                                ? appointment.getDoctor().getId()
                                : null
                )
                .medicalServiceName(
                        appointment != null && appointment.getMedicalService() != null
                                ? appointment.getMedicalService().getName()
                                : null
                )
                .isPaid(appointment != null ? appointment.getIsPaid() : null)
                .receptionistId(
                        appointment != null && appointment.getReceptionist() != null
                                ? appointment.getReceptionist().getId()
                                : null
                )
                .walkInPatientName(appointment != null ? appointment.getWalkInPatientName() : null)
                .build();
    }
}
