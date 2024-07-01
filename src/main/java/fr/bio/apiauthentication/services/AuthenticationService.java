package fr.bio.apiauthentication.services;

import fr.bio.apiauthentication.components.HttpHeadersUtil;
import fr.bio.apiauthentication.dto.MessageResponse;
import fr.bio.apiauthentication.dto.authentication.LoginRequest;
import fr.bio.apiauthentication.dto.authentication.RegisterRequest;
import fr.bio.apiauthentication.entities.Role;
import fr.bio.apiauthentication.entities.User;
import fr.bio.apiauthentication.enums.Messages;
import fr.bio.apiauthentication.enums.TokenType;
import fr.bio.apiauthentication.exceptions.invalid.InvalidCredentialsException;
import fr.bio.apiauthentication.exceptions.not_found.RoleNotFoundException;
import fr.bio.apiauthentication.repositories.RoleRepository;
import fr.bio.apiauthentication.repositories.UserRepository;
import fr.bio.apiauthentication.services.interfaces.IAuthenticationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;

@Service
@RequiredArgsConstructor
@Transactional
public class AuthenticationService implements IAuthenticationService {
    private static final String ROLE = "Role";

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;
    private final HttpHeadersUtil httpHeadersUtil;

    private final UserDetailsService userDetailsService;
    private final JwtService jwtService;

    @Override
    public ResponseEntity<MessageResponse> register(RegisterRequest request) {
        Role role = roleRepository.findByAuthority("USER")
                .orElseThrow(() -> new RoleNotFoundException(Messages.ENTITY_NOT_FOUND.formatMessage(ROLE, "USER")));

        User user = User.builder()
                .firstName(request.firstName())
                .lastName(request.lastName())
                .email(request.email())
                .password(passwordEncoder.encode(request.password()))
                .roles(Collections.singleton(role))
                .build();

        userRepository.save(user);

        return ResponseEntity.ok()
                .body(MessageResponse.fromMessage(Messages.ACCOUNT_CREATED.formatMessage(user.getEmail())));
    }

    @Override
    public ResponseEntity<MessageResponse> login(
            LoginRequest request
    ) {
        final User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new InvalidCredentialsException(Messages.INVALID_CREDENTIALS.formatMessage()));

        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.email(),
                            request.password()
                    )
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);

            final UserDetails userDetails = userDetailsService.loadUserByUsername(user.getEmail());

            final String token = jwtService.generateToken(userDetails);

            jwtService.revokeAllUserTokens(userDetails, TokenType.BEARER);
            jwtService.saveUserToken(userDetails, token, TokenType.BEARER);

            return ResponseEntity.ok()
                    .headers(httpHeadersUtil.createHeaders(token))
                    .body(MessageResponse.fromMessage(Messages.ACCOUNT_CONNECTED.formatMessage(user.getEmail())));
        } catch (BadCredentialsException e) {
            throw new InvalidCredentialsException(Messages.INVALID_CREDENTIALS.formatMessage());
        }
    }
}