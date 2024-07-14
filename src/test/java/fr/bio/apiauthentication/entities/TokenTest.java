package fr.bio.apiauthentication.entities;

import fr.bio.apiauthentication.enums.TokenType;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.Arrays;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@DisplayName("Test Token JPA Entity")
@DataJpaTest
public class TokenTest {
    @Autowired
    private TestEntityManager entityManager;

    private Token token;
    private User user;

    private String tokenString;
    private TokenType tokenType;
    private boolean expired;
    private boolean revoked;

    @BeforeEach
    void setUp() {
        tokenString = RandomStringUtils.randomAlphanumeric(100);
        tokenType = Arrays.stream(TokenType.values()).toList().get((int) (Math.random() * TokenType.values().length));
        expired = Boolean.parseBoolean(RandomStringUtils.randomNumeric(0, 1));
        revoked = Boolean.parseBoolean(RandomStringUtils.randomNumeric(0, 1));

        user = User.builder()
                .email(RandomStringUtils.randomAlphanumeric(10) + "@test.com")
                .password(RandomStringUtils.randomAlphanumeric(30))
                .firstName(RandomStringUtils.randomAlphanumeric(20))
                .lastName(RandomStringUtils.randomAlphanumeric(20))
                .enabled(true)
                .build();
        entityManager.persistAndFlush(user);

        token = Token.builder()
                .token(tokenString)
                .type(tokenType)
                .user(user)
                .expired(expired)
                .revoked(revoked)
                .build();
    }

    @AfterEach
    void tearDown() {
        token = null;
    }

    @Test
    @DisplayName("Test create token")
    public void testCreateToken() {
        final Token savedToken = entityManager.persistAndFlush(token);

        assertThat(savedToken).isNotNull();
        assertThat(savedToken).isEqualTo(token);
        assertThat(savedToken).usingRecursiveComparison().isEqualTo(token);
    }

    @Test
    @DisplayName("Test update token")
    public void testUpdateToken() {
        final Token savedToken = entityManager.persistAndFlush(token);

        long idToken = Long.parseLong(RandomStringUtils.randomNumeric(10));
        tokenString = RandomStringUtils.randomAlphanumeric(100);
        tokenType = Arrays.stream(TokenType.values()).toList().get((int) (Math.random() * TokenType.values().length));
        expired = Boolean.parseBoolean(RandomStringUtils.randomNumeric(0, 1));
        revoked = Boolean.parseBoolean(RandomStringUtils.randomNumeric(0, 1));

        savedToken.setIdToken(idToken);
        savedToken.setToken(tokenString);
        savedToken.setType(tokenType);
        savedToken.setExpired(expired);
        savedToken.setRevoked(revoked);
        savedToken.setUser(user);

        final Token updateToken = entityManager.persist(savedToken);

        assertThat(updateToken).isNotNull();
        assertThat(updateToken).usingRecursiveComparison().isEqualTo(savedToken);
    }

    @Test
    @DisplayName("Test same object")
    public void testEqualsAndHashCode_SameObject() {
        final Token sameToken = token;

        assertThat(sameToken).isEqualTo(token);
        assertThat(sameToken.hashCode()).isEqualTo(token.hashCode());
    }

    @Test
    @DisplayName("Test null")
    public void testEqualsAndHashCode_Null() {
        assertThat((Token) null).isNotEqualTo(token);
        assertThat((Token) null).isNotEqualTo(token.hashCode());
    }

    @Test
    @DisplayName("Test different class")
    public void testEqualsAndHashCode_DifferentClass() {
        assertThat(token).isNotEqualTo("This is a different object");
        assertThat(token.hashCode()).isNotEqualTo("This is a different object".hashCode());
    }

    @Test
    @DisplayName("Test different fields")
    public void testEqualsAndHashCode_DifferentFields() {
        final Token differentFields = Token.builder()
                .token(RandomStringUtils.randomAlphanumeric(100))
                .type(Arrays.stream(TokenType.values()).toList().get((int) (Math.random() * TokenType.values().length)))
                .user(null)
                .revoked(Boolean.parseBoolean(RandomStringUtils.randomNumeric(0, 1)))
                .expired(Boolean.parseBoolean(RandomStringUtils.randomNumeric(0, 1)))
                .build();

        assertThat(differentFields).isNotNull();
        assertThat(token).isNotEqualTo(differentFields);
        assertThat(token.hashCode()).isNotEqualTo(differentFields.hashCode());
    }

    @Test
    @DisplayName("Test same fields")
    public void testEqualsAndHashCode_SameFields() {
        final Token sameFields = Token.builder()
                .token(tokenString)
                .type(tokenType)
                .user(user)
                .expired(expired)
                .revoked(revoked)
                .build();

        assertThat(sameFields).isNotNull();
        assertThat(token).isEqualTo(sameFields);
        assertThat(token.hashCode()).isEqualTo(sameFields.hashCode());
    }

    @Test
    public void testToString() {
        final Token savedToken = entityManager.persistAndFlush(token);
        final String exceptedString = "Token(" +
                "idToken=" + savedToken.getIdToken() + ", " +
                "token=" + savedToken.getToken() + ", " +
                "type=" + savedToken.getType() + ", " +
                "revoked=" + savedToken.isRevoked() + ", " +
                "expired=" + savedToken.isExpired() + ", " +
                "user=" + savedToken.getUser() +
                ")";
        final String exceptedStringLombok = "Token.TokenBuilder(" +
                "idToken=" + savedToken.getIdToken() + ", " +
                "token=" + savedToken.getToken() + ", " +
                "type$value=" + savedToken.getType() + ", " +
                "revoked$value=" + savedToken.isRevoked() + ", " +
                "expired$value=" + savedToken.isExpired() + ", " +
                "user=" + savedToken.getUser() +
                ")";

        assertThat(savedToken).isNotNull();
        assertThat(exceptedString).isEqualTo(savedToken.toString());
        assertThat(Token.builder()
                .idToken(savedToken.getIdToken())
                .token(savedToken.getToken())
                .type(savedToken.getType())
                .user(savedToken.getUser())
                .toString()
        ).isEqualTo(exceptedStringLombok);
    }
}