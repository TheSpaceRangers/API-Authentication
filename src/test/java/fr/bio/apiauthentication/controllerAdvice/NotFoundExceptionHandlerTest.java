package fr.bio.apiauthentication.controllerAdvice;

import fr.bio.apiauthentication.dto.ExceptionResponse;
import fr.bio.apiauthentication.enums.Messages;
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

@DisplayName("Test not fount exception handler")
public class NotFoundExceptionHandlerTest {
    private static final String USER = "User";
    private static final String ROLE = "Role";

    private static final HttpStatus NOT_FOUND = HttpStatus.NOT_FOUND;

    @InjectMocks
    private NotFoundExceptionHandler notFoundExceptionHandler;

    @Mock
    private WebRequest webRequest;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("Test username not found exception")
    public void testUsernameNotFoundException() {
        UsernameNotFoundException exception = new UsernameNotFoundException(Messages.ENTITY_NOT_FOUND.formatMessage(USER, "test@test.com"));
        when(webRequest.getDescription(false)).thenReturn("uri=/api/v1/****");

        ResponseEntity<?> responseEntity = notFoundExceptionHandler.usernameNotFound(exception, webRequest);

        assertThat(responseEntity.getStatusCode()).isEqualTo(NOT_FOUND);

        ExceptionResponse response = (ExceptionResponse) responseEntity.getBody();

        assertThat(response).isNotNull();

        if (response != null) {
            assertThat(response.getErrorMessage()).isEqualTo(Messages.ENTITY_NOT_FOUND.formatMessage(USER, "test@test.com"));
            assertThat(response.getErrorCode()).isEqualTo(NOT_FOUND.value());
            assertThat(response.getErrorDetails()).isEqualTo("uri=/api/v1/****");
            assertThat(response.getTimeStamp()).isEqualTo(LocalDate.now().toEpochDay());
        }
    }

    @Test
    @DisplayName("Test role not found exception")
    public void testRoleNotFoundException() {
        RoleNotFoundException exception = new RoleNotFoundException(Messages.ENTITY_NOT_FOUND.formatMessage(ROLE, "test@test.com"));
        when(webRequest.getDescription(false)).thenReturn("uri=/api/v1/****");

        ResponseEntity<?> responseEntity = notFoundExceptionHandler.roleNotFound(exception, webRequest);

        assertThat(responseEntity.getStatusCode()).isEqualTo(NOT_FOUND);

        ExceptionResponse response = (ExceptionResponse) responseEntity.getBody();

        assertThat(response).isNotNull();

        if (response != null) {
            assertThat(response.getErrorMessage()).isEqualTo(Messages.ENTITY_NOT_FOUND.formatMessage(ROLE, "test@test.com"));
            assertThat(response.getErrorCode()).isEqualTo(NOT_FOUND.value());
            assertThat(response.getErrorDetails()).isEqualTo("uri=/api/v1/****");
            assertThat(response.getTimeStamp()).isEqualTo(LocalDate.now().toEpochDay());
        }
    }
}
