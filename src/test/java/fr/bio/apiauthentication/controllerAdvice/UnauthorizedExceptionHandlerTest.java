package fr.bio.apiauthentication.controllerAdvice;

import fr.bio.apiauthentication.dto.ExceptionResponse;
import fr.bio.apiauthentication.enums.Messages;
import fr.bio.apiauthentication.exceptions.invalid.InvalidCredentialsException;
import fr.bio.apiauthentication.exceptions.invalid.InvalidPasswordException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDate;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.when;

@DisplayName("Test unauthorized exception handler")
public class UnauthorizedExceptionHandlerTest {
    private static final HttpStatus UNAUTHORIZED = HttpStatus.UNAUTHORIZED;

    @InjectMocks
    private UnauthorizedExceptionHandler unauthorizedExceptionHandler;

    @Mock
    private WebRequest webRequest;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("Test invalid credentials exception")
    public void testInvalidCredentialsException() {
        InvalidCredentialsException exception = new InvalidCredentialsException(Messages.INVALID_CREDENTIALS.formatMessage());
        when(webRequest.getDescription(false)).thenReturn("uri=/api/v1/****");

        ResponseEntity<?> responseEntity = unauthorizedExceptionHandler.invalidCredentials(exception, webRequest);

        assertThat(responseEntity.getStatusCode()).isEqualTo(UNAUTHORIZED);

        ExceptionResponse response = (ExceptionResponse) responseEntity.getBody();

        assertThat(response).isNotNull();

        if (response != null) {
            assertThat(response.getErrorMessage()).isEqualTo(Messages.INVALID_CREDENTIALS.formatMessage());
            assertThat(response.getErrorCode()).isEqualTo(UNAUTHORIZED.value());
            assertThat(response.getErrorDetails()).isEqualTo("uri=/api/v1/****");
            assertThat(response.getTimeStamp()).isEqualTo(LocalDate.now());
        }
    }
}
