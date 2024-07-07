package fr.bio.apiauthentication.services;

import fr.bio.apiauthentication.components.HttpHeadersUtil;
import fr.bio.apiauthentication.dto.MessageResponse;
import fr.bio.apiauthentication.dto.authentication.LoginRequest;
import fr.bio.apiauthentication.dto.authentication.RegisterRequest;
import fr.bio.apiauthentication.entities.Role;
import fr.bio.apiauthentication.entities.User;
import fr.bio.apiauthentication.enums.Messages;
import fr.bio.apiauthentication.enums.TokenType;
import fr.bio.apiauthentication.exceptions.already_exists.UserAlreadyExistsException;
import fr.bio.apiauthentication.exceptions.invalid.InvalidCredentialsException;
import fr.bio.apiauthentication.exceptions.not_found.RoleNotFoundException;
import fr.bio.apiauthentication.repositories.RoleRepository;
import fr.bio.apiauthentication.repositories.TokenRepository;
import fr.bio.apiauthentication.repositories.UserRepository;

import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import org.mockito.ArgumentCaptor;
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
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@DisplayName("Test Authentication Service")
public class AuthenticationServiceTest {
    private static final LocalDate NOW = LocalDate.now();

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private UserDetailsService userDetailsService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private HttpHeadersUtil httpHeadersUtil;

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

    private RegisterRequest request;
    private LoginRequest loginRequest;

    private UserDetails userDetails;
    private User user;
    private Role role;

    private String email;
    private String password;
    private String firstName;
    private String lastName;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);

        email = RandomStringUtils.randomAlphanumeric(5).toUpperCase();
        password = RandomStringUtils.randomAlphanumeric(30);
        firstName = RandomStringUtils.randomAlphanumeric(20);
        lastName = RandomStringUtils.randomAlphanumeric(20);

        role = Role.builder()
                .authority(RandomStringUtils.randomAlphanumeric(5).toUpperCase())
                .displayName(RandomStringUtils.randomAlphanumeric(20))
                .description(RandomStringUtils.randomAlphanumeric(20))
                .modifiedAt(NOW)
                .modifiedBy(RandomStringUtils.randomAlphanumeric(30))
                .enabled(true)
                .build();
        roleRepository.save(role);

        user = User.builder()
                .email(email)
                .password(password)
                .firstName(firstName)
                .lastName(lastName)
                .roles(List.of(role))
                .build();
    }

    @Test
    @DisplayName("Test register user")
    public void testRegisterUser_Success() {
        final RegisterRequest request = new RegisterRequest(firstName, lastName, email, password);
        final MessageResponse expectedResponse = MessageResponse.fromMessage(Messages.ACCOUNT_CREATED.formatMessage(email));

        when(roleRepository.findByAuthority("USER")).thenReturn(Optional.of(role));
        when(passwordEncoder.encode(password)).thenReturn(password);
        when(userRepository.save(any(User.class))).thenReturn(user);

        ResponseEntity<MessageResponse> response = authenticationService.register(request);

        final ArgumentCaptor<User> roleCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(roleCaptor.capture());
        final User savedUser = roleCaptor.getValue();

        assertThat(response).isNotNull();
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).usingRecursiveComparison().isEqualTo(expectedResponse);

        assertThat(savedUser).isNotNull();
        assertThat(savedUser.getEmail()).isEqualTo(email);
        assertThat(savedUser.getPassword()).isEqualTo(password);
        assertThat(savedUser.getFirstName()).isEqualTo(firstName);
        assertThat(savedUser.getLastName()).isEqualTo(lastName);
        assertThat(savedUser.getRoles()).isEqualTo(user.getRoles());


        verify(passwordEncoder, times(1)).encode(password);
        verify(userRepository, times(1)).save(any(User.class));
        verify(roleRepository, times(1)).findByAuthority("USER");
    }

    @Test
    @DisplayName("Test register user but role not found")
    public void testRegisterUser_RoleNotFound() {
        final RegisterRequest request = new RegisterRequest(firstName, lastName, email, password);
        final MessageResponse expectedResponse = MessageResponse.fromMessage(Messages.ENTITY_NOT_FOUND.formatMessage("Role", role.getAuthority()));

        when(roleRepository.findByAuthority("USER")).thenReturn(Optional.empty());

        assertThrows(RoleNotFoundException.class, () -> authenticationService.register(request));

        verify(roleRepository, times(1)).findByAuthority("USER");
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("Test login user")
    public void testLogin_Success() {
        final LoginRequest request = new LoginRequest(email, password);
        final MessageResponse expectedResponse = MessageResponse.fromMessage(Messages.ACCOUNT_CONNECTED.formatMessage(email));

        final UserDetails userDetails = org.springframework.security.core.userdetails.User.withUsername(email)
                .password(password)
                .authorities(user.getAuthorities())
                .build();

        final String token = jwtService.generateToken(userDetails);
        final HttpHeaders headers = httpHeadersUtil.createHeaders(token);

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(email, password))).thenReturn(mock(Authentication.class));
        when(userDetailsService.loadUserByUsername(email)).thenReturn(userDetails);
        when(jwtService.generateToken(userDetails)).thenReturn(token);
        when(httpHeadersUtil.createHeaders(token)).thenReturn(headers);

        ResponseEntity<MessageResponse> response = authenticationService.login(request);

        assertThat(response).isNotNull();
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).usingRecursiveComparison().isEqualTo(expectedResponse);

        verify(jwtService, times(1)).revokeAllUserTokens(userDetails, TokenType.BEARER);
        verify(jwtService, times(1)).saveUserToken(userDetails, token, TokenType.BEARER);
    }

    @Test
    @DisplayName("Test login user but user not found")
    public void testLogin_UserNotFound() {
        final LoginRequest request = new LoginRequest(email, password);

        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        assertThrows(InvalidCredentialsException.class, () -> authenticationService.login(request));

        verify(authenticationManager, never()).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(jwtService, never()).generateToken(any(UserDetails.class));
    }

    @Test
    @DisplayName("Test login user but credentials are invalid")
    public void testLogin_InvalidCredentials() {
        final LoginRequest request = new LoginRequest(email, password);

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(email, password))).thenThrow(new InvalidCredentialsException(Messages.INVALID_CREDENTIALS.formatMessage()));

        assertThrows(InvalidCredentialsException.class, () -> authenticationService.login(request));

        verify(authenticationManager, times(1)).authenticate(new UsernamePasswordAuthenticationToken(email, password));
        verify(jwtService, never()).generateToken(any(UserDetails.class));
    }
}