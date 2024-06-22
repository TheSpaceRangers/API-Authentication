package fr.bio.apiauthentication.controllerAdvice;

import fr.bio.apiauthentication.dto.ExceptionResponse;
import fr.bio.apiauthentication.exceptions.InvalidCredentialsException;
import fr.bio.apiauthentication.exceptions.InvalidPasswordException;
import fr.bio.apiauthentication.exceptions.RoleNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDate;

@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<?> usernameNotFound(UsernameNotFoundException exception, WebRequest request) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ExceptionResponse.builder()
                        .timeStamp(LocalDate.now())
                        .errorMessage(exception.getMessage())
                        .errorCode(HttpStatus.NOT_FOUND.value())
                        .errorDetails(request.getDescription(false))
                        .build()
                );
    }

    @ExceptionHandler(RoleNotFoundException.class)
    public ResponseEntity<?> roleNotFound(RoleNotFoundException exception, WebRequest request) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ExceptionResponse.builder()
                        .timeStamp(LocalDate.now())
                        .errorMessage(exception.getMessage())
                        .errorCode(HttpStatus.NOT_FOUND.value())
                        .errorDetails(request.getDescription(false))
                        .build()
                );
    }

    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<?> invalidCredentials(InvalidCredentialsException exception, WebRequest request) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(ExceptionResponse.builder()
                        .timeStamp(LocalDate.now())
                        .errorMessage(exception.getMessage())
                        .errorCode(HttpStatus.UNAUTHORIZED.value())
                        .errorDetails(request.getDescription(false))
                        .build()
                );
    }

    @ExceptionHandler(InvalidPasswordException.class)
    public ResponseEntity<?> invalidPassword(InvalidPasswordException exception, WebRequest request) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(ExceptionResponse.builder()
                        .timeStamp(LocalDate.now())
                        .errorMessage(exception.getMessage())
                        .errorCode(HttpStatus.UNAUTHORIZED.value())
                        .errorDetails(request.getDescription(false))
                        .build()
                );
    }
}
