package fr.bio.apiauthentication.services.interfaces;

import fr.bio.apiauthentication.dto.MessageResponse;
import fr.bio.apiauthentication.dto.authentication.LoginRequest;
import fr.bio.apiauthentication.dto.authentication.RegisterRequest;
import org.springframework.http.ResponseEntity;

public interface IAuthenticationService {
    ResponseEntity<MessageResponse> register(RegisterRequest request);

    ResponseEntity<MessageResponse> login(LoginRequest request);
}
