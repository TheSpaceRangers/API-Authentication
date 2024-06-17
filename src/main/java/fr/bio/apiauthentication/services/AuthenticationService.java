package fr.bio.apiauthentication.services;

import fr.bio.apiauthentication.dto.AuthenticationResponse;
import fr.bio.apiauthentication.dto.CreateUserRequest;
import fr.bio.apiauthentication.entities.User;
import fr.bio.apiauthentication.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationService implements IAuthenticationService {
    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    @Override
    public ResponseEntity<AuthenticationResponse> register(CreateUserRequest request) {
        User user = User.builder()
                .firstName(request.firstName())
                .lastName(request.lastName())
                .email(request.email())
                .password(passwordEncoder.encode(request.password()))
                .build();

        this.userRepository.save(user);

        return ResponseEntity.ok().body(
                AuthenticationResponse.builder()
                        .message("L'utilisateur " + user.getEmail() + " a bien été créé !")
                        .build()
        );
    }
}