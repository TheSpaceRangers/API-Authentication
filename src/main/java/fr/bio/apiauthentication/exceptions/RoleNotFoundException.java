package fr.bio.apiauthentication.exceptions;

public class RoleNotFoundException extends RuntimeException {
    public RoleNotFoundException(
            String message
    ) {
        super(message);
    }
}
