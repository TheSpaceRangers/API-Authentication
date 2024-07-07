package fr.bio.apiauthentication.services;

import fr.bio.apiauthentication.components.HttpHeadersUtil;
import fr.bio.apiauthentication.dto.MessageResponse;
import fr.bio.apiauthentication.dto.reset.ResetPasswordRequest;
import fr.bio.apiauthentication.dto.reset.SendResetEmailRequest;
import fr.bio.apiauthentication.entities.Role;
import fr.bio.apiauthentication.entities.Token;
import fr.bio.apiauthentication.entities.User;
import fr.bio.apiauthentication.enums.Messages;
import fr.bio.apiauthentication.enums.TokenType;
import fr.bio.apiauthentication.exceptions.TokenExpiredException;
import fr.bio.apiauthentication.exceptions.invalid.InvalidTokenException;
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
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@DisplayName("Test reset service")
public class ResetServiceTest {
    private static final LocalDate NOW = LocalDate.now();

    @Mock
    private UserRepository userRepository;

    @Mock
    private TokenRepository tokenRepository;

    @Mock
    private JwtService jwtService;

    @Mock
    private EmailService emailService;

    @Mock
    private UserDetailsService userDetailsService;

    @Mock
    private HttpHeadersUtil httpHeadersUtil;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private ResetService resetService;

    private User user;
    private Token token;

    private String tokenString;

    private HttpHeaders headers;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        Role role = Role.builder()
                .authority(RandomStringUtils.randomAlphanumeric(5).toUpperCase())
                .displayName(RandomStringUtils.randomAlphanumeric(20))
                .description(RandomStringUtils.randomAlphanumeric(20))
                .modifiedAt(NOW)
                .modifiedBy(RandomStringUtils.randomAlphanumeric(30))
                .enabled(true)
                .build();

        user = User.builder()
                .email(RandomStringUtils.randomAlphanumeric(5).toUpperCase())
                .password(RandomStringUtils.randomAlphanumeric(30))
                .firstName(RandomStringUtils.randomAlphanumeric(20))
                .lastName(RandomStringUtils.randomAlphanumeric(20))
                .createdAt(NOW)
                .createdBy(RandomStringUtils.randomAlphanumeric(20))
                .modifiedAt(NOW)
                .modifiedBy(RandomStringUtils.randomAlphanumeric(20))
                .enabled(true)
                .roles(List.of(role))
                .build();

        tokenString = jwtService.generateToken(user);

        token = Token.builder()
                .type(TokenType.PASSWORD_RESET)
                .token(tokenString)
                .user(user)
                .build();

        headers = httpHeadersUtil.createHeaders(tokenString);
    }

    @Test
    @DisplayName("Test password reset email")
    void testPasswordResetEmail() {
        final SendResetEmailRequest request = new SendResetEmailRequest(user.getEmail());
        final MessageResponse exceptedResponse = MessageResponse.fromMessage(Messages.SEND_RESET_MAIL.formatMessage(user.getEmail()));

        final UserDetails userDetails = org.springframework.security.core.userdetails.User.withUsername(user.getEmail())
                .password(user.getPassword())
                .authorities(user.getAuthorities())
                .build();

        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        when(userDetailsService.loadUserByUsername(user.getEmail())).thenReturn(userDetails);
        when(jwtService.generateToken(userDetails)).thenReturn(tokenString);
        when(httpHeadersUtil.createHeaders(tokenString)).thenReturn(headers);

        final ResponseEntity<MessageResponse> response = resetService.passwordResetEmail(tokenString, request);

        assertThat(response).isNotNull();
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).usingRecursiveComparison().isEqualTo(exceptedResponse);

        verify(jwtService).revokeAllUserTokens(userDetails, TokenType.PASSWORD_RESET);
        verify(jwtService).saveUserToken(userDetails, tokenString, TokenType.PASSWORD_RESET);
        verify(emailService).sendPasswordResetEmail(user.getEmail(), tokenString);
    }

    @Test
    @DisplayName("Test password reset email user not found")
    void testPasswordResetEmail_UserNotFound() {
        final SendResetEmailRequest request = new SendResetEmailRequest(user.getEmail());

        when(userRepository.findByEmail(request.email())).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class, () -> resetService.passwordResetEmail(tokenString, request));

        verify(userRepository).findByEmail(request.email());
    }

    @Test
    @DisplayName("Test reset password with valid token")
    void testResetPassword_ValidToken() {
        final String password = RandomStringUtils.randomAlphanumeric(50);

        final ResetPasswordRequest request = new ResetPasswordRequest(password);
        final MessageResponse exceptedResponse = MessageResponse.fromMessage(Messages.PASSWORD_RESET.formatMessage());

        when(tokenRepository.findByToken(tokenString)).thenReturn(Optional.of(token));
        when(jwtService.validateToken(tokenString, user)).thenReturn(true);
        when(passwordEncoder.encode(password)).thenReturn(password);

        ResponseEntity<MessageResponse> response = resetService.resetPassword(tokenString, request);

        final ArgumentCaptor<User> roleCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(roleCaptor.capture());
        final User savedUser = roleCaptor.getValue();

        assertThat(response).isNotNull();
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).usingRecursiveComparison().isEqualTo(exceptedResponse);

        assertThat(savedUser).isNotNull();
        assertThat(savedUser.getPassword()).isEqualTo(password);

        verify(userRepository, times(1)).save(user);
        verify(tokenRepository, times(1)).save(token);
    }

    @Test
    @DisplayName("Test reset password with invalid token")
    void testResetPassword_InvalidToken() {
        final String password = RandomStringUtils.randomAlphanumeric(50);

        ResetPasswordRequest request = new ResetPasswordRequest(password);

        when(tokenRepository.findByToken(tokenString)).thenReturn(Optional.empty());

        assertThrows(InvalidTokenException.class, () -> resetService.resetPassword(tokenString, request));

        verify(tokenRepository, times(1)).findByToken(tokenString);
    }

    @Test
    @DisplayName("Test reset password with expired token")
    void testResetPassword_ExpiredToken() {
        final String password = RandomStringUtils.randomAlphanumeric(50);

        ResetPasswordRequest request = new ResetPasswordRequest(password);

        when(tokenRepository.findByToken(tokenString)).thenReturn(Optional.of(token));
        when(jwtService.validateToken(tokenString, user)).thenReturn(false);

        assertThrows(TokenExpiredException.class, () -> resetService.resetPassword(tokenString, request));

        verify(tokenRepository, times(1)).findByToken(tokenString);
        verify(jwtService, times(1)).validateToken(tokenString, user);
    }
}