package fr.bio.apiauthentication.controllerAdvice;

import fr.bio.apiauthentication.dto.ExceptionResponse;
import fr.bio.apiauthentication.enums.Messages;
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

@DisplayName("Test bad_request exception handler")
public class BadRequestExceptionHandlerTest {
    private static final HttpStatus BAD_REQUEST = HttpStatus.BAD_REQUEST;

    @InjectMocks
    private BadRequestExceptionHandler badRequestExceptionHandler;

    @Mock
    private WebRequest webRequest;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("Test illegal argument exception")
    public void testIllegalArgumentException() {
        IllegalArgumentException exception = new IllegalArgumentException(Messages.STATUS_PARAMETER_INVALID.formatMessage());
        when(webRequest.getDescription(false)).thenReturn("uri=/api/v1/****");

        ResponseEntity<?> responseEntity = badRequestExceptionHandler.invalidArgument(exception, webRequest);

        assertThat(responseEntity.getStatusCode()).isEqualTo(BAD_REQUEST);

        ExceptionResponse response = (ExceptionResponse) responseEntity.getBody();

        assertThat(response).isNotNull();

        if (response != null) {
            assertThat(response.getErrorMessage()).isEqualTo(Messages.STATUS_PARAMETER_INVALID.formatMessage());
            assertThat(response.getErrorCode()).isEqualTo(BAD_REQUEST.value());
            assertThat(response.getErrorDetails()).isEqualTo("uri=/api/v1/****");
            assertThat(response.getTimeStamp()).isEqualTo(LocalDate.now().toEpochDay());
        }
    }
}