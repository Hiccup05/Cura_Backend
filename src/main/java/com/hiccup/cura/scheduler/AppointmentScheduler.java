package com.hiccup.cura.scheduler;

import com.hiccup.cura.enums.AppointmentStatus;
import com.hiccup.cura.model.Appointment;
import com.hiccup.cura.repository.AppointmentRepository;
import com.hiccup.cura.service.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class AppointmentScheduler {
    private final AppointmentRepository appointmentRepository;
    private final EmailService emailService;
    private final Clock clock;

    @Scheduled(fixedRate = 600000)
    public void scheduleAppointments() {
        LocalDateTime now = LocalDateTime.now(clock);
        List<Appointment> confirmedAppointments = appointmentRepository.findByStatus(AppointmentStatus.CONFIRMED);

        List<Appointment> toComplete=confirmedAppointments.stream()
                .filter(a->{
                    LocalDateTime slotEnd=LocalDateTime.of(a.getAppointmentDate(), a.getAppointmentTime());
                    return slotEnd.isBefore(now);
                }).toList();

        if(toComplete.isEmpty()) return;

        toComplete.forEach(a-> {
            a.setStatus(AppointmentStatus.COMPLETED);
            try {
                String email = a.getPatient().getUser().getEmail();
                emailService.sendAppointmentCompletedEmail(email, a);
            } catch (Exception e) {
                log.warn("Failed to send completion email for appointment {}", a.getId());
            }
        });
        appointmentRepository.saveAll(toComplete);

        log.info("Completed appointment are {}", toComplete.size());
    }

    @Scheduled(fixedRate = 600000)
    public void cancelPendingAppointments() {
        Instant now = clock.instant();
        List<Appointment> pendingAppointments = appointmentRepository.findByStatus(AppointmentStatus.PENDING);

        List<Appointment> toCancel = pendingAppointments.stream()
                .filter(a->{
                    return ChronoUnit.HOURS.between(a.getBookedAt(),now)>5;
                }).toList();

        if(toCancel.isEmpty()) return;

        toCancel.forEach(a-> {
            a.setStatus(AppointmentStatus.CANCELLED);
            try {
                String email = a.getPatient().getUser().getEmail();
                emailService.sendAutoCancellationEmail(email, a);
            } catch (Exception e) {
                log.warn("Failed to send auto-cancellation email for appointment {}", a.getId());
            }
        });
        appointmentRepository.saveAll(toCancel);

        log.info("Cancelled appointment are {}", toCancel.size());
    }
}
