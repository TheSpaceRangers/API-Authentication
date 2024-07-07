package fr.bio.apiauthentication.services;

import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;

@DisplayName("Test Email Service")
public class EmailServiceTest {
    @Mock
    private JavaMailSender javaMailSender;

    @InjectMocks
    private EmailService emailService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("Test send email")
    void testSendEmail() {
        final String to = RandomStringUtils.randomAlphanumeric(10) + "@test.com";
        final String subject = RandomStringUtils.randomAlphanumeric(30);
        final String body = RandomStringUtils.randomAlphanumeric(255);

        doNothing().when(javaMailSender).send(any(SimpleMailMessage.class));

        emailService.sendEmail(to, subject, body);

        verify(javaMailSender).send(any(SimpleMailMessage.class));
    }
}