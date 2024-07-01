package fr.bio.apiauthentication.controllerAdvice;

import fr.bio.apiauthentication.dto.ExceptionResponse;
import fr.bio.apiauthentication.exceptions.not_found.RoleNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDate;

@ControllerAdvice
public class NotFoundExceptionHandler {
    private final HttpStatus STATUS = HttpStatus.NOT_FOUND;

    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<?> usernameNotFound(
            UsernameNotFoundException exception,
            WebRequest request
    ) {
        return ResponseEntity.status(STATUS)
                .body(ExceptionResponse.builder()
                        .timeStamp(LocalDate.now())
                        .errorMessage(exception.getMessage())
                        .errorCode(STATUS.value())
                        .errorDetails(request.getDescription(false))
                        .build()
                );
    }

    @ExceptionHandler(RoleNotFoundException.class)
    public ResponseEntity<?> roleNotFound(
            RoleNotFoundException exception,
            WebRequest request
    ) {
        return ResponseEntity.status(STATUS)
                .body(ExceptionResponse.builder()
                        .timeStamp(LocalDate.now())
                        .errorMessage(exception.getMessage())
                        .errorCode(STATUS.value())
                        .errorDetails(request.getDescription(false))
                        .build()
                );
    }
}