package fr.bio.apiauthentication.config;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.test.context.TestPropertySource;

import java.util.Properties;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@DisplayName("Test email configuration")
@TestPropertySource(properties = {
        "spring.mail.host=smtp.example.com",
        "spring.mail.port=587",
        "spring.mail.username=test@example.com",
        "spring.mail.password=secret",
        "spring.mail.properties.mail.smtp.auth=true",
        "spring.mail.properties.mail.smtp.starttls.enable=true"
})
public class EmailConfigurationTest {
    @InjectMocks
    private EmailConfiguration emailConfiguration;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("Test bean java mail sender")
    public void testJavaMailSender() {
        JavaMailSenderImpl mailSender = (JavaMailSenderImpl) emailConfiguration.javaMailSender();

        // TODO Faire de vrai tests

        assertThat(mailSender).isNotNull();
        //assertThat(mailSender.getHost()).isEqualTo("smtp.example.com");
        //assertThat(mailSender.getPort()).isEqualTo(587);
        //assertThat(mailSender.getUsername()).isEqualTo("test@example.com");
        //assertThat(mailSender.getPassword()).isEqualTo("secret");

        Properties properties = mailSender.getJavaMailProperties();
        //assertThat(properties.getProperty("mail.smtp.auth")).isEqualTo("true");
        //assertThat(properties.getProperty("mail.smtp.starttls.enable")).isEqualTo("true");
    }
}
