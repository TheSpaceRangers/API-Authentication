package fr.bio.apiauthentication.controllers;

import fr.bio.apiauthentication.dto.MessageResponse;
import fr.bio.apiauthentication.dto.authentication.LoginRequest;
import fr.bio.apiauthentication.dto.authentication.RegisterRequest;
import fr.bio.apiauthentication.services.interfaces.IAuthenticationService;

import lombok.RequiredArgsConstructor;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api-auth/v1/auth")
@CrossOrigin(origins = "http://localhost:4200")
public class AuthenticationController {
    private final IAuthenticationService authenticationService;

    @PostMapping(value = "/register", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<MessageResponse> register (
            @Validated @RequestBody RegisterRequest request
    ) {
        return authenticationService.register(request);
    }

    @PostMapping(value = "/login", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<MessageResponse> login (
            @Validated @RequestBody LoginRequest request
    ) {
        return authenticationService.login(request);
    }
}