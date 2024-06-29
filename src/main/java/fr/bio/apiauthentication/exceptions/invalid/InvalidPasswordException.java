package fr.bio.apiauthentication.exceptions.invalid;

public class InvalidPasswordException extends RuntimeException {
    public InvalidPasswordException(String message) {
        super(message);
    }
}
