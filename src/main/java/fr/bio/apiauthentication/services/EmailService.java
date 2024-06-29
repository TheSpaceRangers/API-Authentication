package fr.bio.apiauthentication.services;

import fr.bio.apiauthentication.services.interfaces.IEmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService implements IEmailService {

    private final JavaMailSender javaMailSender;

    @Override
    public void sendEmail(
            String to,
            String subject,
            String body
    ) {
        SimpleMailMessage message = new SimpleMailMessage();

        message.setTo(to);
        message.setSubject(subject);
        message.setText(body);

        javaMailSender.send(message);
    }

    @Override
    public void sendPasswordResetEmail(
            String to,
            String token
    ) {
        final String subject = "Reset your password";
        final String resetUrl = "http://localhost:8080/api/v1/reset/password?token=" + token;
        final String message = "To reset your password, click the link below:\n" + resetUrl;

        sendEmail(to, subject, message);
    }
}
