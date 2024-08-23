package fr.bio.apiauthentication.repositories;

import fr.bio.apiauthentication.entities.Token;
import fr.bio.apiauthentication.entities.User;
import fr.bio.apiauthentication.enums.TokenType;
import jakarta.transaction.Transactional;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Arrays;
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
        userRepository.save(user);

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
        assertThat(savedToken.getToken()).isEqualTo(tokenString);
        assertThat(savedToken.getType()).isEqualTo(tokenType);
        assertThat(savedToken.getUser()).isEqualTo(user);
        assertThat(savedToken.isExpired()).isEqualTo(expired);
        assertThat(savedToken.isRevoked()).isEqualTo(revoked);
    }

    @Test
    @DisplayName("Test find token by token")
    public void testFindByToken() {
        Token savedToken = tokenRepository.save(token);

        Optional<Token> foundToken = tokenRepository.findByToken(savedToken.getToken());

        assertThat(foundToken.isPresent()).isTrue();

        if (foundToken.isPresent()) {
            assertThat(foundToken.get().getToken()).isEqualTo(tokenString);
            assertThat(foundToken.get().getType()).isEqualTo(tokenType);
            assertThat(foundToken.get().getUser()).isEqualTo(user);
            assertThat(foundToken.get().isExpired()).isEqualTo(expired);
            assertThat(foundToken.get().isRevoked()).isEqualTo(revoked);
        }
    }

    @Test
    @DisplayName("Test find token by user id")
    public void testFindByUser_IdUser() {
        Token savedToken_1 = tokenRepository.save(token);
        Token savedToken_2 = tokenRepository.save(generateToken(false, false));

        List<Token> exceptedTokens = List.of(savedToken_1, savedToken_2);
        List<Token> foundTokens = tokenRepository.findAllByUser_IdUser(user.getIdUser());

        assertThat(foundTokens).isEqualTo(exceptedTokens);
        assertThat(foundTokens.size()).isEqualTo(exceptedTokens.size());
        assertThat(foundTokens.get(0)).isEqualTo(savedToken_1);
        assertThat(foundTokens.get(1)).isEqualTo(savedToken_2);
    }

    @Test
    @DisplayName("Test find token by user email")
    public void testFindByUser_Email() {
        Token savedToken_1 = tokenRepository.save(token);
        Token savedToken_2 = tokenRepository.save(generateToken(false, false));

        List<Token> exceptedTokens = List.of(savedToken_1, savedToken_2);
        List<Token> foundTokens = tokenRepository.findAllByUser_Email(user.getEmail());

        assertThat(foundTokens).isEqualTo(exceptedTokens);
        assertThat(foundTokens.size()).isEqualTo(exceptedTokens.size());
        assertThat(foundTokens.get(0)).isEqualTo(savedToken_1);
        assertThat(foundTokens.get(1)).isEqualTo(savedToken_2);
    }

    @Test
    @DisplayName("Test find all valid token by user")
    public void testFindByUserAndTypeAndExpiredAndRevoked() {
        Token expiredToken = tokenRepository.save(generateToken(true, true));
        Token validToken = tokenRepository.save(generateToken(false, false));

        List<Token> exceptedTokensValid = List.of(validToken);
        List<Token> exceptedTokensExpired = List.of(expiredToken);

        List<Token> foundedTokensValid = tokenRepository.findByUserAndTypeAndExpiredFalseAndRevokedFalse(user, TokenType.BEARER);
        List<Token> foundedTokensExpired = tokenRepository.findByUserAndTypeAndExpiredTrueAndRevokedTrue(user, TokenType.BEARER);

        assertThat(foundedTokensValid).isEqualTo(exceptedTokensValid);
        assertThat(foundedTokensValid).usingRecursiveComparison().isEqualTo(exceptedTokensValid);

        assertThat(foundedTokensExpired).isEqualTo(exceptedTokensExpired);
        assertThat(foundedTokensExpired).usingRecursiveComparison().isEqualTo(exceptedTokensExpired);
    }

    @Test
    @DisplayName("Test delete token")
    public void testDeleteToken() {
        Token savedToken = tokenRepository.save(token);

        tokenRepository.delete(savedToken);

        Optional<Token> foundToken = tokenRepository.findByToken(savedToken.getToken());

        assertThat(foundToken.isPresent()).isFalse();
    }

    @Test
    @DisplayName("Test delete token")
    public void testDeleteByUserAndType() {
        Token savedToken = tokenRepository.save(token);

        tokenRepository.deleteByUserAndType(user, tokenType);

        Optional<Token> foundToken = tokenRepository.findByToken(savedToken.getToken());

        assertThat(foundToken.isPresent()).isFalse();
    }

    private Token generateToken(
            boolean expired,
            boolean revoked
    ) {
        return Token.builder()
                .token(RandomStringUtils.randomAlphanumeric(100))
                .type(TokenType.BEARER)
                .user(user)
                .revoked(expired)
                .expired(revoked)
                .build();
    }
}