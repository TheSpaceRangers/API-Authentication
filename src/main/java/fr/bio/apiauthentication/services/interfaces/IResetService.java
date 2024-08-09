package fr.bio.apiauthentication.services.interfaces;

import fr.bio.apiauthentication.dto.MessageResponse;
import fr.bio.apiauthentication.dto.reset.ResetPasswordRequest;
import fr.bio.apiauthentication.dto.reset.SendResetEmailRequest;
import org.springframework.http.ResponseEntity;

public interface IResetService {
    ResponseEntity<MessageResponse> passwordResetEmail(String token, SendResetEmailRequest request);

    ResponseEntity<MessageResponse> resetPassword(String token, ResetPasswordRequest request);
}