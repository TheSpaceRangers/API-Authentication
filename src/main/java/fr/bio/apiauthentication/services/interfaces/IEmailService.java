package fr.bio.apiauthentication.services.interfaces;

public interface IEmailService {
    void sendEmail(String to, String subject, String body);
}
