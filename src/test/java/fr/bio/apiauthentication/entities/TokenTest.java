package fr.bio.apiauthentication.entities;

import fr.bio.apiauthentication.enums.TokenType;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@DisplayName("Test Token JPA Entity")
@DataJpaTest
public class TokenTest {
    @Autowired
    private TestEntityManager entityManager;

    private User user;
    private Token token;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .email("c.tronel@test.properties.com")
                .password("password")
                .firstName("firstName")
                .lastName("lastName")
                .enabled(true)
                .build();
        entityManager.persist(user);

        token = Token.builder()
                .token("This is a test.properties")
                .type(TokenType.BEARER)
                .user(user)
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
    }

    @Test
    @DisplayName("Test update token")
    public void testUpdateToken() {
        Token savedToken = entityManager.persistAndFlush(token);

        savedToken.setToken("This is a second test.properties");
        savedToken.setType(TokenType.BEARER);
        savedToken.setUser(user);
        savedToken.setExpired(true);
        savedToken.setRevoked(true);

        Token savedToken2 = entityManager.persistAndFlush(savedToken);

        assertThat(savedToken2).isNotNull();
        assertThat(savedToken2).usingRecursiveComparison().isEqualTo(savedToken);
    }

    @Test
    @DisplayName("Test same object")
    public void testEquals_SameObject() {
        assertThat(token).isEqualTo(token);
    }

    @Test
    @DisplayName("Test null")
    public void testEquals_Null() {
        assertThat(token).isNotEqualTo(null);
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
                .token("This is a test.properties with different fields")
                .user(null)
                .revoked(true)
                .expired(true)
                .build();

        assertThat(token).isNotEqualTo(differentFields);
    }

    @Test
    @DisplayName("Test same fields")
    public void testEquals_SameFields() {
        Token sameFields = Token.builder()
                .token("This is a test.properties")
                .user(user)
                .build();

        assertThat(token).isEqualTo(sameFields);
    }

    @Test
    @DisplayName("Test same fields hashcode")
    public void testHashCode_SameFields() {
        Token sameFields = Token.builder()
                .token("This is a test.properties")
                .user(user)
                .build();

        assertThat(token.hashCode()).isEqualTo(sameFields.hashCode());
    }
}
