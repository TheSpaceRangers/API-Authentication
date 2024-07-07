package fr.bio.apiauthentication.enums;

public enum Messages {
    // ENTITY
    ENTITY_NOT_FOUND("%s '%s' not found."),
    ENTITY_CREATED("%s '%s' has been created."),
    ENTITY_ALREADY_EXISTS("%s '%s' already exists."),
    ENTITY_UPDATED("%s '%s' has been updated."),
    ENTITY_NO_MODIFIED("No modifications were made to the %s '%s'."),
    ENTITY_ACTIVATED("%s '%s' has been activated."),
    ENTITY_DEACTIVATED("%s '%s' has been deactivated."),

    // Reset
    SEND_RESET_MAIL("An email with reset instructions has been sent to %s."),
    INVALID_TOKEN("The provided token is invalid."),
    EXPIRED_TOKEN("The provided token has expired."),
    PASSWORD_RESET("Your password has been successfully reset."),

    // Account
    ACCOUNT_CREATED("Account %s has been created."),
    ACCOUNT_CONNECTED("Account %s is connected."),
    ACCOUNT_UPDATED("Account %s has been updated."),
    INVALID_CREDENTIALS("Incorrect email and/or password."),

    // Mail configuration
    MAIL_HOST("Mail server host must be provided"),
    MAIL_PORT("Mail server port must be provided"),
    MAIL_USERNAME("Mail username must be provided"),
    MAIL_PASSWORD("Mail password must be provided"),

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
