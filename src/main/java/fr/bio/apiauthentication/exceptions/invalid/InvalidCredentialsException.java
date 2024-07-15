package fr.bio.apiauthentication.exceptions.invalid;

public class InvalidCredentialsException extends RuntimeException {
    public InvalidCredentialsException(String message) {
        super(message);
    }
}
