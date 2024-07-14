package fr.bio.apiauthentication.config;

import fr.bio.apiauthentication.enums.Messages;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Properties;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DisplayName("Test email configuration")
@ExtendWith(MockitoExtension.class)
public class EmailConfigurationTest {

    @InjectMocks
    private EmailConfiguration emailConfiguration;

    @Value("${spring.mail.host}")
    private String host = "smtp.test.com";

    @Value("${spring.mail.port}")
    private int port = 587;

    @Value("${spring.mail.username}")
    private String username = "test@test.com";

    @Value("${spring.mail.password}")
    private String password = "password";

    @Value("${spring.mail.properties.mail.smtp.auth}")
    private boolean auth = true;

    @Value("${spring.mail.properties.mail.smtp.starttls.enable}")
    private boolean starttls = true;

    @BeforeEach
    public void setUp() {
        ReflectionTestUtils.setField(emailConfiguration, "host", host);
        ReflectionTestUtils.setField(emailConfiguration, "port", port);
        ReflectionTestUtils.setField(emailConfiguration, "username", username);
        ReflectionTestUtils.setField(emailConfiguration, "password", password);
        ReflectionTestUtils.setField(emailConfiguration, "auth", auth);
        ReflectionTestUtils.setField(emailConfiguration, "starttls", starttls);
    }

    @Test
    @DisplayName("Test validateProperties with valid values")
    public void testValidateProperties_Success() {
        emailConfiguration.validateProperties();
    }

    @Test
    @DisplayName("Test validateProperties with empty host")
    void testValidateProperties_EmptyHost() {
        ReflectionTestUtils.setField(emailConfiguration, "host", "");

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> emailConfiguration.validateProperties());

        assertThat(exception.getMessage()).isEqualTo(Messages.MAIL_HOST.formatMessage());
    }

    @Test
    @DisplayName("Test validateProperties with invalid port")
    void testValidateProperties_InvalidPort() {
        ReflectionTestUtils.setField(emailConfiguration, "port", -1);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> emailConfiguration.validateProperties());

        assertThat(exception.getMessage()).isEqualTo(Messages.MAIL_PORT.formatMessage());
    }

    @Test
    @DisplayName("Test validateProperties with empty username")
    void testValidateProperties_EmptyUsername() {
        ReflectionTestUtils.setField(emailConfiguration, "username", "");

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> emailConfiguration.validateProperties());

        assertThat(exception.getMessage()).isEqualTo(Messages.MAIL_USERNAME.formatMessage());
    }

    @Test
    @DisplayName("Test validateProperties with empty password")
    void testValidateProperties_EmptyPassword() {
        ReflectionTestUtils.setField(emailConfiguration, "password", "");

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> emailConfiguration.validateProperties());

        assertThat(exception.getMessage()).isEqualTo(Messages.MAIL_PASSWORD.formatMessage());
    }

    @Test
    @DisplayName("Test JavaMailSender Bean")
    public void testJavaMailSender() {
        final JavaMailSender mailSender = emailConfiguration.javaMailSender();
        assertThat(mailSender).isNotNull();
        assertThat(mailSender).isInstanceOf(JavaMailSenderImpl.class);

        final JavaMailSenderImpl mailSenderImpl = (JavaMailSenderImpl) mailSender;
        assertThat(mailSenderImpl.getHost()).isEqualTo(host);
        assertThat(mailSenderImpl.getPort()).isEqualTo(port);
        assertThat(mailSenderImpl.getUsername()).isEqualTo(username);
        assertThat(mailSenderImpl.getPassword()).isEqualTo(password);
    }
}