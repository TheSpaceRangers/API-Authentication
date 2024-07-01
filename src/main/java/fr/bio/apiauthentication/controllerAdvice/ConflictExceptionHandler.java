package fr.bio.apiauthentication.controllerAdvice;

import fr.bio.apiauthentication.dto.ExceptionResponse;
import fr.bio.apiauthentication.exceptions.already_exists.RoleAlreadyExistsException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDate;

@ControllerAdvice
public class ConflictExceptionHandler {
    private static final HttpStatus CONFLICT = HttpStatus.CONFLICT;

    @ExceptionHandler(RoleAlreadyExistsException.class)
    public ResponseEntity<?> roleAlreadyExists(RoleAlreadyExistsException exception, WebRequest request) {
        return ResponseEntity.status(CONFLICT)
                .body(ExceptionResponse.builder()
                        .timeStamp(LocalDate.now())
                        .errorMessage(exception.getMessage())
                        .errorCode(CONFLICT.value())
                        .errorDetails(request.getDescription(false))
                        .build()
                );
    }
}