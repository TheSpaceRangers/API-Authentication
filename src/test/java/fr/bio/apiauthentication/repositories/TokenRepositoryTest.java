package fr.bio.apiauthentication.repositories;

import fr.bio.apiauthentication.entities.Token;
import fr.bio.apiauthentication.entities.User;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@DisplayName("Test Token JPA Repository")
@DataJpaTest
@Transactional
public class TokenRepositoryTest {
    @Autowired
    private TokenRepository tokenRepository;

    @Autowired
    private UserRepository userRepository;

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
        userRepository.save(user);

        token = Token.builder()
                .token("This is a test")
                .user(user)
                .build();
    }

    @AfterEach
    void tearDown() {
        userRepository.deleteAll();
        tokenRepository.deleteAll();

        user = null;
        token = null;
    }

    @Test
    @DisplayName("Test save token")
    public void testSaveToken() {
        Token savedToken = tokenRepository.save(token);

        assertThat(savedToken).isNotNull();
    }

    @Test
    @DisplayName("Test find token by token")
    public void testFindByToken() {
        Token savedToken = tokenRepository.save(token);

        Optional<Token> foundToken = tokenRepository.findByToken(savedToken.getToken());

        assertThat(foundToken.isPresent()).isTrue();
    }

    @Test
    @DisplayName("Test delete token")
    public void testDeleteToken() {
        Token savedToken = tokenRepository.save(token);

        tokenRepository.delete(savedToken);

        Optional<Token> foundToken = tokenRepository.findByToken(savedToken.getToken());

        assertThat(foundToken.isPresent()).isFalse();
    }
}
