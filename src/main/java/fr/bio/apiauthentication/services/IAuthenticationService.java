package fr.bio.apiauthentication.services;

import fr.bio.apiauthentication.dto.AuthenticationRequest;
import fr.bio.apiauthentication.dto.AuthenticationResponse;
import fr.bio.apiauthentication.dto.CreateUserRequest;
import org.springframework.http.ResponseEntity;

public interface IAuthenticationService {
    ResponseEntity<AuthenticationResponse> register(CreateUserRequest request);

    ResponseEntity<AuthenticationResponse> login(AuthenticationRequest request);
}
