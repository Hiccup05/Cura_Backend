package com.hiccup.cura.service.appointment;

import com.hiccup.cura.exception.AppointmentNotFound;
import com.hiccup.cura.mapper.AppointmentMapper;
import com.hiccup.cura.model.Appointment;
import com.hiccup.cura.repository.AppointmentRepository;
import com.hiccup.cura.request.AppointmentDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class AppointmentService {
    private final AppointmentRepository appointmentRepository;
    private final AppointmentMapper appointmentMapper;

    public AppointmentDto createAppointment(AppointmentDto appointmentDto){
        Appointment appointment = appointmentMapper.dtoToAppointment(appointmentDto);
        Appointment newAppointment= appointmentRepository.save(appointment);
        return appointmentMapper.appointmentToDto(newAppointment);
    }

    public AppointmentDto getAppointment(Long id){
       return appointmentRepository.findById(id)
               .map(appointmentMapper::appointmentToDto)
               .orElseThrow(()->new AppointmentNotFound("Appointment might be canceled or never booked"));
    }

    public void updateAppointment(AppointmentDto appointmentDto, Long id){
        appointmentRepository.findById(id).ifPresentOrElse(appointmentInDb->{
            appointmentInDb.setProduct(appointmentDto.getProduct());
            appointmentInDb.setDoctor(appointmentDto.getDoctor());
            appointmentInDb.setStatus(appointmentDto.getStatus());
            appointmentInDb.setCreatedAt(appointmentDto.getCreatedAt());
            appointmentRepository.save(appointmentInDb);
        },()->{throw new AppointmentNotFound("Appointment might be canceled or never booked");});
    }

    public void deleteAppointment(Long id){
        appointmentRepository.findById(id)
                .ifPresentOrElse(appointmentRepository::delete,
                        ()->{throw new AppointmentNotFound("Appointment might be canceled or never booked");});
    }
}
