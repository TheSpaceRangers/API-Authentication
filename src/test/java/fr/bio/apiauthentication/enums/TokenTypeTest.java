package fr.bio.apiauthentication.enums;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@DisplayName("Test enum token type")
public class TokenTypeTest {
    @Test
    public void testTokenType() {
        assertThat(TokenType.valueOf("BEARER")).isEqualTo(TokenType.BEARER);
        assertThat(TokenType.valueOf("PASSWORD_RESET")).isEqualTo(TokenType.PASSWORD_RESET);
    }
}