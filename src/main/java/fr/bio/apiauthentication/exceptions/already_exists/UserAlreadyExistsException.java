package fr.bio.apiauthentication.exceptions.already_exists;

public class UserAlreadyExistsException extends RuntimeException {
    public UserAlreadyExistsException (String message) {
        super(message);
    }
}