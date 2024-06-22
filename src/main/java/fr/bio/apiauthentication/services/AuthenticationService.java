package fr.bio.apiauthentication.services;

import fr.bio.apiauthentication.dto.authentication.AuthenticationRequest;
import fr.bio.apiauthentication.dto.authentication.AuthenticationResponse;
import fr.bio.apiauthentication.dto.authentication.CreateUserRequest;
import fr.bio.apiauthentication.entities.Role;
import fr.bio.apiauthentication.entities.Token;
import fr.bio.apiauthentication.entities.User;
import fr.bio.apiauthentication.exceptions.InvalidCredentialsException;
import fr.bio.apiauthentication.exceptions.RoleNotFoundException;
import fr.bio.apiauthentication.repositories.RoleRepository;
import fr.bio.apiauthentication.repositories.TokenRepository;
import fr.bio.apiauthentication.repositories.UserRepository;
import fr.bio.apiauthentication.services.interfaces.IAuthenticationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AuthenticationService implements IAuthenticationService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final TokenRepository tokenRepository;

    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;

    private final UserDetailsService userDetailsService;
    private final JwtService jwtService;

    @Override
    public ResponseEntity<AuthenticationResponse> register(CreateUserRequest request) {
        Role role = roleRepository.findByRoleName("USER").orElseThrow(
                () -> new RoleNotFoundException("Role 'USER' not found")
        );

        User user = User.builder()
                .firstName(request.firstName())
                .lastName(request.lastName())
                .email(request.email())
                .password(passwordEncoder.encode(request.password()))
                .roles(Collections.singleton(role))
                .build();

        userRepository.save(user);

        return ResponseEntity.ok().body(
                AuthenticationResponse.builder()
                        .message("L'utilisateur " + user.getEmail() + " a bien été créé !")
                        .build()
        );
    }

    @Override
    public ResponseEntity<AuthenticationResponse> login(AuthenticationRequest request) {
        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.email(),
                            request.password()
                    )
            );
            SecurityContextHolder.getContext().setAuthentication(authentication);
        } catch (InvalidCredentialsException e) {
            throw new InvalidCredentialsException("Email et/ou mot de passe incorecte");
        }

        final UserDetails userDetails = userDetailsService.loadUserByUsername(user.getEmail());

        final String token = jwtService.generateToken(userDetails);

        revokeAllUserTokens(userDetails);
        saveUserToken(userDetails, token);

        HttpHeaders headers = new HttpHeaders();
        headers.add("Access-Control-Expose-Headers", "Authorization");
        headers.add("Authorization", "Bearer " + token);

        return ResponseEntity.ok()
                .headers(headers)
                .body(AuthenticationResponse.builder()
                        .message("L'utilisateur " + request.email() + " est connecté !")
                        .build()
                );
    }

    private void saveUserToken(
            UserDetails userDetails,
            String strToken
    ) {
        User user = userRepository.findByEmail(userDetails.getUsername()).orElseThrow(
                () -> new UsernameNotFoundException("User not found")
        );

        Token token = Token.builder()
                .token(strToken)
                .user(user)
                .build();

        tokenRepository.save(token);
    }

    private void revokeAllUserTokens(UserDetails userDetails) {
        User user = userRepository.findByEmail(userDetails.getUsername()).orElseThrow(
                () -> new UsernameNotFoundException("User not found")
        );

        List<Token> tokens = tokenRepository.findAllValidTokenByUser(user.getIdUser());
        tokens.forEach(token -> {
            token.setExpired(true);
            token.setRevoked(true);
        });
        tokenRepository.saveAll(tokens);
    }
}