package fr.bio.apiauthentication.services;

import fr.bio.apiauthentication.components.HttpHeadersUtil;
import fr.bio.apiauthentication.components.JwtTokenUtil;
import fr.bio.apiauthentication.dto.authentication.AuthenticationRequest;
import fr.bio.apiauthentication.dto.authentication.AuthenticationResponse;
import fr.bio.apiauthentication.dto.authentication.CreateUserRequest;
import fr.bio.apiauthentication.entities.Role;
import fr.bio.apiauthentication.entities.User;
import fr.bio.apiauthentication.enums.TokenType;
import fr.bio.apiauthentication.exceptions.InvalidCredentialsException;
import fr.bio.apiauthentication.exceptions.RoleNotFoundException;
import fr.bio.apiauthentication.repositories.RoleRepository;
import fr.bio.apiauthentication.repositories.UserRepository;
import fr.bio.apiauthentication.services.interfaces.IAuthenticationService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
@RequiredArgsConstructor
@Transactional
public class AuthenticationService implements IAuthenticationService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;
    private final HttpHeadersUtil httpHeadersUtil;
    private final JwtTokenUtil jwtTokenUtil;

    private final UserDetailsService userDetailsService;
    private final JwtService jwtService;

    @Override
    public ResponseEntity<AuthenticationResponse> register(CreateUserRequest request) {
        Role role = roleRepository.findByAuthority("USER")
                .orElseThrow(() -> new RoleNotFoundException("Role 'USER' not found"));

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
                .orElseThrow(() -> new InvalidCredentialsException("Incorrect email and/or password"));

        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.email(),
                            request.password()
                    )
            );
            SecurityContextHolder.getContext().setAuthentication(authentication);
        } catch (InvalidCredentialsException e) {
            throw new InvalidCredentialsException("Incorrect email and/or password");
        }

        final UserDetails userDetails = userDetailsService.loadUserByUsername(user.getEmail());

        final String token = jwtService.generateToken(userDetails);

        jwtTokenUtil.revokeAllUserTokens(userDetails, TokenType.BEARER);
        jwtTokenUtil.saveUserToken(userDetails, token);

        return ResponseEntity.ok()
                .headers(httpHeadersUtil.createHeaders(token))
                .body(AuthenticationResponse.builder()
                        .message("L'utilisateur " + request.email() + " est connecté !")
                        .build()
                );
    }
}