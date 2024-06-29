package fr.bio.apiauthentication.controllerAdvice;

import fr.bio.apiauthentication.dto.ExceptionResponse;
import fr.bio.apiauthentication.exceptions.invalid.InvalidCredentialsException;
import fr.bio.apiauthentication.exceptions.invalid.InvalidPasswordException;
import fr.bio.apiauthentication.exceptions.not_found.RoleNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDate;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.when;

@DisplayName("Test global exception handler")
public class GlobalExceptionHandlerTest {
    @InjectMocks
    private GlobalExceptionHandler globalExceptionHandler;

    @Mock
    private WebRequest webRequest;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("Test username not found exception")
    public void testUsernameNotFoundException() {
        UsernameNotFoundException exception = new UsernameNotFoundException("User not found");
        when(webRequest.getDescription(false)).thenReturn("User details");

        ResponseEntity<?> responseEntity = globalExceptionHandler.usernameNotFound(exception, webRequest);

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);

        ExceptionResponse response = (ExceptionResponse) responseEntity.getBody();

        assertThat(response).isNotNull();
        assertThat(response.getErrorMessage()).isEqualTo("User not found");
        assertThat(response.getErrorCode()).isEqualTo(HttpStatus.NOT_FOUND.value());
        assertThat(response.getErrorDetails()).isEqualTo("User details");
        assertThat(response.getTimeStamp()).isEqualTo(LocalDate.now());
    }

    @Test
    @DisplayName("Test role not found exception")
    public void testRoleNotFoundException() {
        RoleNotFoundException exception = new RoleNotFoundException("Role not found");
        when(webRequest.getDescription(false)).thenReturn("Role details");

        ResponseEntity<?> responseEntity = globalExceptionHandler.roleNotFound(exception, webRequest);

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        ExceptionResponse response = (ExceptionResponse) responseEntity.getBody();
        assertThat(response).isNotNull();
        assertThat(response.getErrorMessage()).isEqualTo("Role not found");
        assertThat(response.getErrorCode()).isEqualTo(HttpStatus.NOT_FOUND.value());
        assertThat(response.getErrorDetails()).isEqualTo("Role details");
        assertThat(response.getTimeStamp()).isEqualTo(LocalDate.now());
    }

    @Test
    @DisplayName("Test invalid credentials exception")
    public void testInvalidCredentialsException() {
        InvalidCredentialsException exception = new InvalidCredentialsException("Invalid credentials");
        when(webRequest.getDescription(false)).thenReturn("Invalid credentials details");

        ResponseEntity<?> responseEntity = globalExceptionHandler.invalidCredentials(exception, webRequest);

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        ExceptionResponse response = (ExceptionResponse) responseEntity.getBody();
        assertThat(response).isNotNull();
        assertThat(response.getErrorMessage()).isEqualTo("Invalid credentials");
        assertThat(response.getErrorCode()).isEqualTo(HttpStatus.UNAUTHORIZED.value());
        assertThat(response.getErrorDetails()).isEqualTo("Invalid credentials details");
        assertThat(response.getTimeStamp()).isEqualTo(LocalDate.now());
    }

    @Test
    @DisplayName("Test invalid password exception")
    public void testInvalidPasswordException() {
        InvalidPasswordException exception = new InvalidPasswordException("Invalid password");
        when(webRequest.getDescription(false)).thenReturn("Invalid password details");

        ResponseEntity<?> responseEntity = globalExceptionHandler.invalidPassword(exception, webRequest);

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        ExceptionResponse response = (ExceptionResponse) responseEntity.getBody();
        assertThat(response).isNotNull();
        assertThat(response.getErrorMessage()).isEqualTo("Invalid password");
        assertThat(response.getErrorCode()).isEqualTo(HttpStatus.UNAUTHORIZED.value());
        assertThat(response.getErrorDetails()).isEqualTo("Invalid password details");
        assertThat(response.getTimeStamp()).isEqualTo(LocalDate.now());
    }
}