package fr.bio.apiauthentication.enums;

public enum Messages {
    // USER

    // ROLE
    ROLE_NOT_FOUND("Role %s has been not found"),
    ROLE_CREATED("Role %s has been created"),
    ROLE_ALREADY_EXISTS("Role %s already exists"),
    ROLE_UPDATED("Role %s has been updated"),
    ROLE_NO_MODIFIED("No modifications were made to the role %s"),
    ROLE_ACTIVATED("Role %s has been activated"),
    ROLE_DEACTIVATED("Role %s has been deactivated");

    private final String message;

    Messages(String message) {
        this.message = message;
    }

    public String formatMessage(String message) {
        return String.format(this.message, message);
    }
}
