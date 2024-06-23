package fr.bio.apiauthentication.enums;

public enum Messages {
    // USER

    // ROLE
    ROLE_NOT_FOUND("Role %s has been not found"),
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
