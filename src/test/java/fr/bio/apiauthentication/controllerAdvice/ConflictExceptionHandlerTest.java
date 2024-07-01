package fr.bio.apiauthentication.controllerAdvice;

import fr.bio.apiauthentication.dto.ExceptionResponse;
import fr.bio.apiauthentication.enums.Messages;
import fr.bio.apiauthentication.exceptions.already_exists.RoleAlreadyExistsException;
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

@DisplayName("Test conflict exception handler")
public class ConflictExceptionHandlerTest {
    private static final String ROLE = "ROLE";

    private static final HttpStatus CONFLICT = HttpStatus.CONFLICT;

    @InjectMocks
    private ConflictExceptionHandler conflictExceptionHandler;

    @Mock
    private WebRequest webRequest;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("Test role already exists exception")
    public void testRoleAlreadyExistsException() {
        RoleAlreadyExistsException exception = new RoleAlreadyExistsException(Messages.ENTITY_ALREADY_EXISTS.formatMessage(ROLE, "USER"));
        when(webRequest.getDescription(false)).thenReturn("uri=/api/v1/****");

        ResponseEntity<?> responseEntity = conflictExceptionHandler.roleAlreadyExists(exception, webRequest);

        assertThat(responseEntity.getStatusCode()).isEqualTo(CONFLICT);

        ExceptionResponse response = (ExceptionResponse) responseEntity.getBody();

        assertThat(response).isNotNull();

        if (response != null) {
            assertThat(response.getErrorMessage()).isEqualTo(Messages.ENTITY_ALREADY_EXISTS.formatMessage(ROLE, "USER"));
            assertThat(response.getErrorCode()).isEqualTo(CONFLICT.value());
            assertThat(response.getErrorDetails()).isEqualTo("uri=/api/v1/****");
            assertThat(response.getTimeStamp()).isEqualTo(LocalDate.now());
        }
    }
}
