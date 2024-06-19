package fr.bio.apiauthentication.entities;

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
                .email("c.tronel@test.com")
                .password("password")
                .firstName("firstName")
                .lastName("lastName")
                .enabled(true)
                .build();
        entityManager.persist(user);

        token = Token.builder()
                .token("This is a test")
                .user(user)
                .build();
    }

    @AfterEach
    void tearDown() {
        entityManager.remove(user);

        token = null;
    }

    @Test
    public void testCreateToken() {
        Token savedToken = entityManager.persistAndFlush(token);

        assertThat(savedToken).isNotNull();
    }

    @Test
    public void testUpdateToken() {
        Token savedToken = entityManager.persistAndFlush(token);

        savedToken.setToken("This is a second test");
        savedToken.setExpired(true);
        savedToken.setRevoked(true);

        Token savedToken2 = entityManager.persistAndFlush(savedToken);

        assertThat(savedToken2).isNotNull();
        assertThat(savedToken2).usingRecursiveComparison().isEqualTo(savedToken);
    }

    @Test
    public void testEquals_SameObject() {
        assertThat(token).isEqualTo(token);
    }

    @Test
    public void testEquals_Null() {
        assertThat(token).isNotEqualTo(null);
    }

    @Test
    public void testEquals_DifferentClass() {
        assertThat(token).isNotEqualTo("This is a different object");
    }

    @Test
    public void testEquals_DifferentFields() {
        Token differentFields = Token.builder()
                .token("This is a test with different fields")
                .user(null)
                .revoked(true)
                .expired(true)
                .build();

        assertThat(token).isNotEqualTo(differentFields);
    }

    @Test
    public void testEquals_SameFields() {
        Token sameFields = Token.builder()
                .token("This is a test")
                .user(user)
                .build();

        assertThat(token).isEqualTo(sameFields);
    }

    @Test
    public void testHashCode_SameFields() {
        Token sameFields = Token.builder()
                .token("This is a test")
                .user(user)
                .build();

        assertThat(token.hashCode()).isEqualTo(sameFields.hashCode());
    }
}
