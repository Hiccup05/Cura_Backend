package com.hiccup.cura.service;

import com.hiccup.cura.model.Appointment;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {
    private final JavaMailSender mailSender;

    @Value("${auth.token.jwt}")
    private String hostEmail;


    public void sendEmail(String to, String subject, String text) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(hostEmail);
        message.setTo(to);
        message.setSubject(subject);
        message.setText(text);

        mailSender.send(message);
    }

    public void sendAppointmentEmail(String to, Appointment appointment) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setTo(to);
            helper.setSubject("Appointment Confirmation");

            String content = buildAppointmentEmailTemplate(appointment);

            helper.setText(content, true);

            mailSender.send(message);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String buildAppointmentEmailTemplate(Appointment appointment) {
        return """
        <div style="font-family: Arial, sans-serif; padding: 20px;">
            <h2 style="color: #2c3e50;">Appointment Confirmed ✅</h2>

            <p>Hello,</p>

            <p>Your appointment has been successfully booked. Here are the details:</p>

            <table style="border-collapse: collapse; width: 100%; margin-top: 10px;">
                <tr>
                    <td style="padding: 8px; font-weight: bold;">Doctor:</td>
                    <td style="padding: 8px;">Dr. %s</td>
                </tr>
                <tr>
                    <td style="padding: 8px; font-weight: bold;">Service:</td>
                    <td style="padding: 8px;">%s</td>
                </tr>
                <tr>
                    <td style="padding: 8px; font-weight: bold;">Date:</td>
                    <td style="padding: 8px;">%s</td>
                </tr>
                <tr>
                    <td style="padding: 8px; font-weight: bold;">Time:</td>
                    <td style="padding: 8px;">%s</td>
                </tr>
                <tr>
                    <td style="padding: 8px; font-weight: bold;">Status:</td>
                    <td style="padding: 8px;">%s</td>
                </tr>
            </table>

            <p style="margin-top: 20px;">
                Please arrive at least <b>10 minutes early</b>.
            </p>

            <p>If you need to cancel, please do so within the allowed time.</p>

            <br/>

            <p>Thank you,<br/>Cura Healthcare Team</p>
        </div>
        """.formatted(
                appointment.getDoctor().getFirstName() + " " + appointment.getDoctor().getLastName(),
                appointment.getMedicalService().getName(),
                appointment.getAppointmentDate(),
                appointment.getAppointmentTime(),
                appointment.getStatus()
        );
    }
}
