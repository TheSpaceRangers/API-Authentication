package fr.bio.apiauthentication.exceptions.not_found;

public class RoleNotFoundException extends RuntimeException {
    public RoleNotFoundException(
            String message
    ) {
        super(message);
    }
}
