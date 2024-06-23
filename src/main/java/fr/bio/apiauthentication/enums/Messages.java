package fr.bio.apiauthentication.enums;

public enum Messages {
    // ENTITY
    ENTITY_NOT_FOUND("%s %s not found"),
    ENTITY_CREATED("%s %s has been created"),
    ENTITY_ALREADY_EXISTS("%s %s already exists"),
    ENTITY_UPDATED("%s %s has been updated"),
    ENTITY_NO_MODIFIED("No modifications were made to the %s %s"),
    ENTITY_ACTIVATED("%s %s has been activated"),
    ENTITY_DEACTIVATED("%s %s has been deactivated"),

    // Paremeters
    STATUS_PARAMETER_INVALID("Invalid action parameter. Use 'activate' or 'deactivate'.");

    private final String message;

    Messages(String message) {
        this.message = message;
    }

    public String formatMessage(Object... args) {
        return String.format(this.message, args);
    }
}
