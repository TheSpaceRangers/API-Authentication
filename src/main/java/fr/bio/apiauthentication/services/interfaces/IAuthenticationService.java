package fr.bio.apiauthentication.services.interfaces;

import fr.bio.apiauthentication.dto.authentication.AuthenticationRequest;
import fr.bio.apiauthentication.dto.authentication.AuthenticationResponse;
import fr.bio.apiauthentication.dto.authentication.CreateUserRequest;
import org.springframework.http.ResponseEntity;

public interface IAuthenticationService {
    ResponseEntity<AuthenticationResponse> register(CreateUserRequest request);

    ResponseEntity<AuthenticationResponse> login(AuthenticationRequest request);
}
