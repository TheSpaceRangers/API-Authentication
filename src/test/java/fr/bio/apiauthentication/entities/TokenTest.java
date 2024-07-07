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
        entityManager.persist(user);

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
        entityManager.remove(user);

        token = null;
    }

    @Test
    @DisplayName("Test create token")
    public void testCreateToken() {
        Token savedToken = entityManager.persistAndFlush(token);

        assertThat(savedToken).isNotNull();
        assertThat(savedToken).isNotEqualTo(token);
    }

    @Test
    @DisplayName("Test update token")
    public void testUpdateToken() {
        Token savedToken = entityManager.persistAndFlush(token);

        tokenString = RandomStringUtils.randomAlphanumeric(100);
        tokenType = Arrays.stream(TokenType.values()).toList().get((int) (Math.random() * TokenType.values().length));
        expired = Boolean.parseBoolean(RandomStringUtils.randomNumeric(0, 1));
        revoked = Boolean.parseBoolean(RandomStringUtils.randomNumeric(0, 1));

        savedToken.setToken(tokenString);
        savedToken.setType(tokenType);
        savedToken.setExpired(expired);
        savedToken.setRevoked(revoked);

        Token updateToken = entityManager.persistAndFlush(savedToken);

        assertThat(updateToken).isNotNull();
        assertThat(updateToken).usingRecursiveComparison().isEqualTo(savedToken);
    }

    @Test
    @DisplayName("Test same object")
    public void testEquals_SameObject() {
        Token sameToken = token;

        assertThat(sameToken).isEqualTo(token);
    }

    @Test
    @DisplayName("Test null")
    public void testEquals_Null() {
        assertThat((Token) null).isNotEqualTo(token);
    }

    @Test
    @DisplayName("Test different class")
    public void testEquals_DifferentClass() {
        assertThat(token).isNotEqualTo("This is a different object");
    }

    @Test
    @DisplayName("Test different fields")
    public void testEquals_DifferentFields() {
        Token differentFields = Token.builder()
                .token(RandomStringUtils.randomAlphanumeric(100))
                .type(Arrays.stream(TokenType.values()).toList().get((int) (Math.random() * TokenType.values().length)))
                .user(null)
                .revoked(Boolean.parseBoolean(RandomStringUtils.randomNumeric(0, 1)))
                .expired(Boolean.parseBoolean(RandomStringUtils.randomNumeric(0, 1)))
                .build();

        assertThat(differentFields).isNotNull();
        assertThat(token).isNotEqualTo(differentFields);
    }

    @Test
    @DisplayName("Test same fields")
    public void testEquals_SameFields() {
        Token sameFields = Token.builder()
                .token(tokenString)
                .type(tokenType)
                .user(user)
                .expired(expired)
                .revoked(revoked)
                .build();

        assertThat(sameFields).isNotNull();
        assertThat(token).isEqualTo(sameFields);
    }

    @Test
    @DisplayName("Test same fields hashcode")
    public void testHashCode_SameFields() {
        Token sameFields = Token.builder()
                .token(tokenString)
                .type(tokenType)
                .user(user)
                .expired(expired)
                .revoked(revoked)
                .build();

        assertThat(sameFields).isNotNull();
        assertThat(token.hashCode()).isEqualTo(sameFields.hashCode());
    }
}