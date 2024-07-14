package fr.bio.apiauthentication.config;

import ch.qos.logback.core.util.StringUtil;
import fr.bio.apiauthentication.enums.Messages;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.util.StringUtils;

import java.util.Properties;

@Configuration
public class EmailConfiguration {
    @Value("${spring.mail.host}")
    private String host;

    @Value("${spring.mail.port}")
    private int port;

    @Value("${spring.mail.username}")
    private String username;

    @Value("${spring.mail.password}")
    private String password;

    @Value("${spring.mail.properties.mail.smtp.auth}")
    private boolean auth;

    @Value("${spring.mail.properties.mail.smtp.starttls.enable}")
    private boolean starttls;

    @PostConstruct
    public void validateProperties() {
        if (!StringUtils.hasText(host))
            throw new IllegalArgumentException(Messages.MAIL_HOST.formatMessage());

        if (port <= 0)
            throw new IllegalArgumentException(Messages.MAIL_PORT.formatMessage());

        if (!StringUtils.hasText(username))
            throw new IllegalArgumentException(Messages.MAIL_USERNAME.formatMessage());

        if (!StringUtils.hasText(password))
            throw new IllegalArgumentException(Messages.MAIL_PASSWORD.formatMessage());
    }

    @Bean
    public JavaMailSender javaMailSender() {
        final JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost(host);
        mailSender.setPort(port);
        mailSender.setUsername(username);
        mailSender.setPassword(password);

        final Properties properties = mailSender.getJavaMailProperties();
        properties.put("mail.smtp.auth", auth);
        properties.put("mail.smtp.starttls.enable", starttls);

        return mailSender;
    }
}