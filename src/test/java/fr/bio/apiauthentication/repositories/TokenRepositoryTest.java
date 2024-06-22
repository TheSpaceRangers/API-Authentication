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

    @BeforeEach
    void setUp() {
        user = User.builder()
                .email("c.tronel@test.properties.com")
                .password("password")
                .firstName("firstName")
                .lastName("lastName")
                .enabled(true)
                .build();
        userRepository.save(user);
    }

    @AfterEach
    void tearDown() {
        userRepository.deleteAll();
        tokenRepository.deleteAll();

        user = null;
    }

    @Test
    @DisplayName("Test save token")
    public void testSaveToken() {
        Token token = Token.builder()
                .token("Save token")
                .user(user)
                .build();
        Token savedToken = tokenRepository.save(token);

        assertThat(savedToken).isNotNull();
    }

    @Test
    @DisplayName("Test find token by token")
    public void testFindByToken() {
        Token token = Token.builder()
                .token("Find token")
                .user(user)
                .build();
        Token savedToken = tokenRepository.save(token);

        Optional<Token> foundToken = tokenRepository.findByToken(savedToken.getToken());

        assertThat(foundToken.isPresent()).isTrue();
    }

    @Test
    @DisplayName("Test find token by user id")
    public void testFindByUser_IdUser() {
        Token token = Token.builder()
                .token("Find token by id")
                .user(user)
                .build();
        Token savedToken = tokenRepository.save(token);

        List<Token> actualTokens = List.of(savedToken);
        List<Token> foundTokens = tokenRepository.findAllByUser_IdUser(user.getIdUser());

        assertThat(actualTokens).isEqualTo(foundTokens);
    }

    @Test
    @DisplayName("Test find token by user email")
    public void testFindByUser_Email() {
        Token token = Token.builder()
                .token("Find token by email")
                .user(user)
                .build();
        Token savedToken = tokenRepository.save(token);

        List<Token> actualTokens = List.of(savedToken);
        List<Token> foundTokens = tokenRepository.findAllByUser_Email(user.getEmail());

        assertThat(actualTokens).isEqualTo(foundTokens);
    }

    @Test
    @DisplayName("Test find all valid token by user")
    public void testFindAllValidTokenByUser() {
        Token expiredToken = Token.builder()
                .token("Find all valid token expired")
                .expired(true)
                .revoked(true)
                .user(user)
                .build();
        tokenRepository.save(expiredToken);
        Token validToken = Token.builder()
                .token("Find all valid token")
                .user(user)
                .build();
        tokenRepository.save(validToken);


        List<Token> actualTokens = List.of(validToken);
        List<Token> foundTokens = tokenRepository.findAllValidTokenByUser(user.getIdUser());

        assertThat(actualTokens).isEqualTo(foundTokens);
    }


    @Test
    @DisplayName("Test delete token")
    public void testDeleteToken() {
        Token token = Token.builder()
                .token("Delete token")
                .user(user)
                .build();
        Token savedToken = tokenRepository.save(token);

        tokenRepository.delete(savedToken);

        Optional<Token> foundToken = tokenRepository.findByToken(savedToken.getToken());

        assertThat(foundToken.isPresent()).isFalse();
    }
}
