package fr.bio.apiauthentication.controllerAdvice;

import fr.bio.apiauthentication.dto.ExceptionResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

@ControllerAdvice
public class BadRequestExceptionHandler {
    private static final HttpStatus BAD_REQUEST = HttpStatus.BAD_REQUEST;

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<?> invalidArgument(IllegalArgumentException exception, WebRequest request) {
        return ResponseEntity.status(BAD_REQUEST)
                .body(ExceptionResponse.fromErrorMessage(exception.getMessage(), BAD_REQUEST.value(), request.getDescription(false)));
    }
}