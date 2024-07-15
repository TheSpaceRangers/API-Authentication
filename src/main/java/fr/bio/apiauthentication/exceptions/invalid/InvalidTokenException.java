package fr.bio.apiauthentication.exceptions.invalid;

public class InvalidTokenException extends RuntimeException {
    public InvalidTokenException(
            String message
    ) {
        super(message);
    }
}
