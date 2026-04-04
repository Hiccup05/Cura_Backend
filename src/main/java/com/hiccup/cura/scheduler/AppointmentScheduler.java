package com.hiccup.cura.scheduler;

import com.hiccup.cura.enums.AppointmentStatus;
import com.hiccup.cura.model.Appointment;
import com.hiccup.cura.repository.AppointmentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class AppointmentScheduler {
    private final AppointmentRepository appointmentRepository;

    @Scheduled(fixedRate = 60000)
    public void scheduleAppointments() {
        LocalDateTime now = LocalDateTime.now();
        List<Appointment> confirmedAppointments = appointmentRepository.findByStatus(AppointmentStatus.CONFIRMED);

        List<Appointment> toComplete=confirmedAppointments.stream()
                .filter(a->{
                    LocalDateTime slotEnd=LocalDateTime.of(a.getAppointmentDate(), a.getAppointmentTime());
                    return slotEnd.isBefore(now);
                }).toList();

        if(toComplete.isEmpty()) return;

        toComplete.forEach(a-> a.setStatus(AppointmentStatus.CONFIRMED));
        appointmentRepository.saveAll(toComplete);

        log.info("Completed appointment are {}", toComplete.size());
    }
}
