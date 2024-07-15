package fr.bio.apiauthentication.enums;

import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@DisplayName("Test enum messages")
public class MessagesTest {
    @Test
    public void testFormatMessage() {
        final String entity = "User";
        final String entityName = RandomStringUtils.randomAlphanumeric(20);

        String expectedMessage = "User '" + entityName + "' not found.";
        String actualMessage = Messages.ENTITY_NOT_FOUND.formatMessage(entity, entityName);
        assertThat(actualMessage).isEqualTo(expectedMessage);

        expectedMessage = "User '" + entityName + "' has been created.";
        actualMessage = Messages.ENTITY_CREATED.formatMessage(entity, entityName);
        assertThat(actualMessage).isEqualTo(expectedMessage);

        expectedMessage = "User '" + entityName + "' already exists.";
        actualMessage = Messages.ENTITY_ALREADY_EXISTS.formatMessage(entity, entityName);
        assertThat(actualMessage).isEqualTo(expectedMessage);

        expectedMessage = "User '" + entityName + "' has been updated.";
        actualMessage = Messages.ENTITY_UPDATED.formatMessage(entity, entityName);
        assertThat(actualMessage).isEqualTo(expectedMessage);

        expectedMessage = "No modifications were made to the User '" + entityName + "'.";
        actualMessage = Messages.ENTITY_NO_MODIFIED.formatMessage(entity, entityName);
        assertThat(actualMessage).isEqualTo(expectedMessage);

        expectedMessage = "User '" + entityName + "' has been activated.";
        actualMessage = Messages.ENTITY_ACTIVATED.formatMessage(entity, entityName);
        assertThat(actualMessage).isEqualTo(expectedMessage);

        expectedMessage = "User '" + entityName + "' has been deactivated.";
        actualMessage = Messages.ENTITY_DEACTIVATED.formatMessage(entity, entityName);
        assertThat(actualMessage).isEqualTo(expectedMessage);

        final String email = RandomStringUtils.randomAlphanumeric(20) + "@test.com";
        expectedMessage = "An email with reset instructions has been sent to '" + email + "'.";
        actualMessage = Messages.SEND_RESET_MAIL.formatMessage(email);
        assertThat(actualMessage).isEqualTo(expectedMessage);

        expectedMessage = "The provided token is invalid.";
        actualMessage = Messages.INVALID_TOKEN.formatMessage();
        assertThat(actualMessage).isEqualTo(expectedMessage);

        expectedMessage = "The provided token has expired.";
        actualMessage = Messages.EXPIRED_TOKEN.formatMessage();
        assertThat(actualMessage).isEqualTo(expectedMessage);

        expectedMessage = "Your password has been successfully reset.";
        actualMessage = Messages.PASSWORD_RESET.formatMessage();
        assertThat(actualMessage).isEqualTo(expectedMessage);

        expectedMessage = "Account '" + entityName + "' has been created.";
        actualMessage = Messages.ACCOUNT_CREATED.formatMessage(entityName);
        assertThat(actualMessage).isEqualTo(expectedMessage);

        expectedMessage = "Account '" + entityName + "' is connected.";
        actualMessage = Messages.ACCOUNT_CONNECTED.formatMessage(entityName);
        assertThat(actualMessage).isEqualTo(expectedMessage);

        expectedMessage = "Account '" + entityName + "' has been updated.";
        actualMessage = Messages.ACCOUNT_UPDATED.formatMessage(entityName);
        assertThat(actualMessage).isEqualTo(expectedMessage);

        expectedMessage = "Incorrect email and/or password.";
        actualMessage = Messages.INVALID_CREDENTIALS.formatMessage();
        assertThat(actualMessage).isEqualTo(expectedMessage);

        expectedMessage = "Mail server host must be provided";
        actualMessage = Messages.MAIL_HOST.formatMessage();
        assertThat(actualMessage).isEqualTo(expectedMessage);

        expectedMessage = "Mail server port must be provided";
        actualMessage = Messages.MAIL_PORT.formatMessage();
        assertThat(actualMessage).isEqualTo(expectedMessage);

        expectedMessage = "Mail username must be provided";
        actualMessage = Messages.MAIL_USERNAME.formatMessage();
        assertThat(actualMessage).isEqualTo(expectedMessage);

        expectedMessage = "Mail password must be provided";
        actualMessage = Messages.MAIL_PASSWORD.formatMessage();
        assertThat(actualMessage).isEqualTo(expectedMessage);

        expectedMessage = "Invalid action parameter. Use 'activate' or 'deactivate'.";
        actualMessage = Messages.STATUS_PARAMETER_INVALID.formatMessage();
        assertThat(actualMessage).isEqualTo(expectedMessage);
    }

    @Test
    public void testEnumValues() {
        assertThat(Messages.valueOf("ENTITY_NOT_FOUND")).isEqualTo(Messages.ENTITY_NOT_FOUND);
        assertThat(Messages.valueOf("ENTITY_CREATED")).isEqualTo(Messages.ENTITY_CREATED);
        assertThat(Messages.valueOf("SEND_RESET_MAIL")).isEqualTo(Messages.SEND_RESET_MAIL);
        assertThat(Messages.valueOf("INVALID_TOKEN")).isEqualTo(Messages.INVALID_TOKEN);
    }
}