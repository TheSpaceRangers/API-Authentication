package fr.bio.apiauthentication.controllerAdvice;

import fr.bio.apiauthentication.dto.ExceptionResponse;
import fr.bio.apiauthentication.exceptions.invalid.InvalidCredentialsException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDate;

@ControllerAdvice
public class UnauthorizedExceptionHandler {
    private static final HttpStatus UNAUTHORIZED = HttpStatus.UNAUTHORIZED;

    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<?> invalidCredentials(InvalidCredentialsException exception, WebRequest request) {
        return ResponseEntity.status(UNAUTHORIZED)
                .body(ExceptionResponse.builder()
                        .timeStamp(LocalDate.now())
                        .errorMessage(exception.getMessage())
                        .errorCode(UNAUTHORIZED.value())
                        .errorDetails(request.getDescription(false))
                        .build()
                );
    }
}