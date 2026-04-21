package com.hiccup.cura.service;

import com.hiccup.cura.model.Appointment;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String hostEmail;


    public void sendEmail(String to, String subject, String text) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(hostEmail);
        message.setTo(to);
        message.setSubject(subject);
        message.setText(text);

        mailSender.send(message);
    }
    public void sendWelcomeEmail(String to, String name) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setTo(to);
            helper.setSubject("Welcome to Cura Healthcare 🎉");

            String content = buildWelcomeTemplate(name);

            helper.setText(content, true);

            mailSender.send(message);

        } catch (Exception e) {
            e.printStackTrace(); // log only
        }
    }

    private String buildWelcomeTemplate(String name) {
        return """
        <div style="font-family: Arial, sans-serif; padding: 20px;">
            <h2 style="color: #3498db;">Welcome to Cura Healthcare 🎉</h2>

            <p>Hello %s,</p>

            <p>
                Your account has been successfully created. We're glad to have you with us!
            </p>

            <p>
                You can now:
            </p>

            <ul>
                <li>Book appointments with doctors</li>
                <li>View your appointment history</li>
                <li>Manage your profile</li>
            </ul>

            <p style="margin-top: 20px;">
                Start by booking your first appointment anytime.
            </p>

            <br/>

            <p>Thank you,<br/>Cura Healthcare Team</p>
        </div>
        """.formatted(name);
    }

    public void sendDoctorPromotionEmail(String to, String name) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setTo(to);
            helper.setSubject("You are now a Doctor on Cura 👨‍⚕️");

            String content = """
            <div style="font-family: Arial, sans-serif; padding: 20px;">
                <h2 style="color: #2ecc71;">Congratulations 🎉</h2>

                <p>Hello %s,</p>

                <p>
                    You have been successfully promoted to a <b>Doctor</b> role in Cura Healthcare.
                </p>

                <p>
                    You can now:
                </p>

                <ul>
                    <li>Manage your appointments</li>
                    <li>View patient bookings</li>
                    <li>Provide prescriptions</li>
                </ul>

                <br/>

                <p>Thank you,<br/>Cura Healthcare Team</p>
            </div>
        """.formatted(name);

            helper.setText(content, true);
            mailSender.send(message);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void sendReceptionistPromotionEmail(String to, String name) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setTo(to);
            helper.setSubject("You are now a Receptionist on Cura 👨‍⚕️");

            String content = """
            <div style="font-family: Arial, sans-serif; padding: 20px;">
                <h2 style="color: #2ecc71;">Congratulations 🎉</h2>

                <p>Hello %s,</p>

                <p>
                    You have been successfully promoted to a <b>Receptionist</b> role in Cura Healthcare.
                </p>

                <p>
                    You can now:
                </p>

                <ul>
                    <li>Manage your appointments</li>
                    <li>Create patient appointments</li>
                </ul>

                <br/>

                <p>Thank you,<br/>Cura Healthcare Team</p>
            </div>
        """.formatted(name);

            helper.setText(content, true);
            mailSender.send(message);

        } catch (Exception e) {
            e.printStackTrace();
        }
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
        String template = """
    <div style="font-family: Arial, sans-serif; padding: 20px;">
        <h2 style="color: #2c3e50;">Appointment Confirmed ✅</h2>

        <p>Hello,</p>

        <p>Your appointment has been successfully booked. Here are the details:</p>

        <table style="border-collapse: collapse; width: 100%%; margin-top: 10px;">
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
    """;

        return String.format(template,
                appointment.getDoctor().getFirstName() + " " + appointment.getDoctor().getLastName(),
                appointment.getMedicalService().getName(),
                appointment.getAppointmentDate(),
                appointment.getAppointmentTime(),
                appointment.getStatus()
        );
    }

    public void sendCancellationEmail(String to, Appointment appointment) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setTo(to);
            helper.setSubject("Appointment Cancelled");

            String content = buildCancellationTemplate(appointment);

            helper.setText(content, true);

            mailSender.send(message);

        } catch (Exception e) {
            e.printStackTrace(); // log only
        }
    }

    private String buildCancellationTemplate(Appointment appointment) {
        String template = """
        <div style="font-family: Arial, sans-serif; padding: 20px;">
            <h2 style="color: #e74c3c;">Appointment Cancelled ❌</h2>
            <p>Hello,</p>
            <p>Your appointment has been <b>successfully cancelled</b>. Below were the details:</p>
            <table style="border-collapse: collapse; width: 100%%; margin-top: 10px;">
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
            </table>
            <p style="margin-top: 20px;">If this was a mistake, you can book a new appointment anytime.</p>
            <br/>
            <p>Thank you,<br/>Cura Healthcare Team</p>
        </div>
        """;
        return String.format(template,
                appointment.getDoctor().getFirstName() + " " + appointment.getDoctor().getLastName(),
                appointment.getMedicalService().getName(),
                appointment.getAppointmentDate(),
                appointment.getAppointmentTime()
        );
    }

    public void sendPaymentSuccessEmail(String to, Appointment appointment) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setTo(to);
            helper.setSubject("Payment Successful - Appointment Confirmed");

            String content = buildPaymentSuccessTemplate(appointment);

            helper.setText(content, true);

            mailSender.send(message);

        } catch (Exception e) {
            e.printStackTrace(); // log only
        }
    }

    private String buildPaymentSuccessTemplate(Appointment appointment) {
        String template = """
        <div style="font-family: Arial, sans-serif; padding: 20px;">
            <h2 style="color: #27ae60;">Payment Successful 💳</h2>
            <p>Hello,</p>
            <p>Your payment has been <b>successfully processed</b>. Your appointment is now confirmed.</p>
            <table style="border-collapse: collapse; width: 100%%; margin-top: 10px;">
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
                    <td style="padding: 8px; font-weight: bold;">Amount Paid:</td>
                    <td style="padding: 8px;">Rs. %s</td>
                </tr>
                <tr>
                    <td style="padding: 8px; font-weight: bold;">Payment Method:</td>
                    <td style="padding: 8px;">%s</td>
                </tr>
            </table>
            <p style="margin-top: 20px;">Please keep this email as your payment confirmation.</p>
            <p>We look forward to serving you. Please arrive at least <b>10 minutes early</b>.</p>
            <br/>
            <p>Thank you,<br/>Cura Healthcare Team</p>
        </div>
        """;
        return String.format(template,
                appointment.getDoctor().getFirstName() + " " + appointment.getDoctor().getLastName(),
                appointment.getMedicalService().getName(),
                appointment.getAppointmentDate(),
                appointment.getAppointmentTime(),
                appointment.getMedicalService().getPrice(),
                appointment.getPaymentMethod()
        );
    }

    public void sendAutoCancellationEmail(String to, Appointment appointment) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setTo(to);
            helper.setSubject("Appointment Auto-Cancelled - Payment Not Received");

            String content = buildAutoCancellationTemplate(appointment);

            helper.setText(content, true);

            mailSender.send(message);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String buildAutoCancellationTemplate(Appointment appointment) {
        String template = """
    <div style="font-family: Arial, sans-serif; padding: 20px;">
        <h2 style="color: #e74c3c;">Appointment Auto-Cancelled ⏰</h2>
        <p>Hello,</p>
        <p>
            Your appointment has been <b>automatically cancelled</b> because payment
            was not completed within the allowed time window.
        </p>
        <table style="border-collapse: collapse; width: 100%%; margin-top: 10px;">
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
        </table>
        <p style="margin-top: 20px;">
            If you still need this appointment, please book again and complete payment promptly.
        </p>
        <br/>
        <p>Thank you,<br/>Cura Healthcare Team</p>
    </div>
    """;
        return String.format(template,
                appointment.getDoctor().getFirstName() + " " + appointment.getDoctor().getLastName(),
                appointment.getMedicalService().getName(),
                appointment.getAppointmentDate(),
                appointment.getAppointmentTime()
        );
    }

    public void sendAppointmentCompletedEmail(String to, Appointment appointment) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setTo(to);
            helper.setSubject("Appointment Completed - Thank You for Visiting Cura");

            String content = buildAppointmentCompletedTemplate(appointment);

            helper.setText(content, true);

            mailSender.send(message);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String buildAppointmentCompletedTemplate(Appointment appointment) {
        String template = """
    <div style="font-family: Arial, sans-serif; padding: 20px;">
        <h2 style="color: #27ae60;">Appointment Completed ✅</h2>
        <p>Hello,</p>
        <p>Your appointment has been marked as <b>completed</b>. We hope your visit went well!</p>
        <table style="border-collapse: collapse; width: 100%%; margin-top: 10px;">
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
        </table>
        <p style="margin-top: 20px;">
            If you received a prescription, you can view it anytime from your dashboard.
        </p>
        <p>We hope to see you again soon. Take care!</p>
        <br/>
        <p>Thank you,<br/>Cura Healthcare Team</p>
    </div>
    """;
        return String.format(template,
                appointment.getDoctor().getFirstName() + " " + appointment.getDoctor().getLastName(),
                appointment.getMedicalService().getName(),
                appointment.getAppointmentDate(),
                appointment.getAppointmentTime()
        );
    }
}
