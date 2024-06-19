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

import jakarta.transaction.Transactional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Collections;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@DisplayName("Test Authentication Service")
public class AuthenticationServiceTest {
    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private UserDetailsService userDetailsService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private TokenRepository tokenRepository;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private AuthenticationService authenticationService;

    private CreateUserRequest request;
    private AuthenticationRequest authenticationRequest;

    private UserDetails userDetails;
    private User user;
    private Role role;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);

        request = new CreateUserRequest(
                "Charles",
                "TRONEL",
                "c.tronel@test.com",
                "1234567890"
        );

        authenticationRequest = new AuthenticationRequest(
                "c.tronel@test.com",
                "1234567890"
        );

        user = User.builder()
                .firstName("Charles")
                .lastName("Tronel")
                .email("c.tronel@test.com")
                .password("1234567890")
                .build();

        userDetails = org.springframework.security.core.userdetails.User.withUsername("c.tronel@test.com")
                .password("1234567890")
                .authorities(Collections.emptyList())
                .build();

        role = Role.builder()
                .roleName("USER")
                .build();
        roleRepository.save(role);
    }

    @Test
    @Transactional
    public void testRegisterUser() {
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(user);
        when(roleRepository.findByRoleName("USER")).thenReturn(Optional.of(role));

        ResponseEntity<AuthenticationResponse> response = authenticationService.register(request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().getMessage()).isEqualTo("L'utilisateur c.tronel@test.com a bien été créé !");

        verify(passwordEncoder, times(1)).encode("1234567890");
        verify(userRepository, times(1)).save(any(User.class));
        verify(roleRepository, times(1)).findByRoleName("USER");
    }

    @Test
    public void testRegisterUserRoleNotExists() {
        when(roleRepository.findByRoleName("USER")).thenReturn(Optional.empty());

        RoleNotFoundException exception = assertThrows(
                RoleNotFoundException.class,
                () -> authenticationService.register(request)
        );

        assertThat(exception.getMessage()).isEqualTo("Role 'USER' not found");

        verify(roleRepository, times(1)).findByRoleName("USER");
        verify(passwordEncoder, never()).encode(anyString());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    public void testLoginSuccess() {
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(mock(Authentication.class));
        when(userDetailsService.loadUserByUsername(anyString())).thenReturn(userDetails);
        when(jwtService.generateToken(any(UserDetails.class))).thenReturn("jwt-token");
        when(jwtService.generateRefreshToken(any(UserDetails.class))).thenReturn("refresh-token");
        when(tokenRepository.findAllValidTokenByUser(anyLong())).thenReturn(Collections.emptyList());

        ResponseEntity<AuthenticationResponse> response = authenticationService.login(authenticationRequest);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getHeaders().getFirst(HttpHeaders.AUTHORIZATION)).isEqualTo("Bearer jwt-token");
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getMessage()).isEqualTo("L'utilisateur c.tronel@test.com est connecté !");

        verify(tokenRepository, times(1)).save(any(Token.class));
        verify(tokenRepository, times(1)).findAllValidTokenByUser(anyLong());
    }

    @Test
    public void testLoginUserNotFound() {
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class, () -> authenticationService.login(authenticationRequest));

        verify(authenticationManager, never()).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(jwtService, never()).generateToken(any(UserDetails.class));
    }

    @Test
    public void testLoginInvalidCredentials() {
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new InvalidCredentialsException("Email et/ou mot de passe incorrecte"));

        assertThrows(InvalidCredentialsException.class, () -> authenticationService.login(authenticationRequest));

        verify(authenticationManager, times(1)).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(jwtService, never()).generateToken(any(UserDetails.class));
    }
}