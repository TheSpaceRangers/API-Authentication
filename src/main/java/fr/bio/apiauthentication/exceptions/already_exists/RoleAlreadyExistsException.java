package fr.bio.apiauthentication.exceptions.already_exists;

public class RoleAlreadyExistsException extends RuntimeException {
    public RoleAlreadyExistsException(String message) {
        super(message);
    }
}