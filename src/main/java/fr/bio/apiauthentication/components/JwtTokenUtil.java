package fr.bio.apiauthentication.components;

import fr.bio.apiauthentication.entities.Token;
import fr.bio.apiauthentication.entities.User;
import fr.bio.apiauthentication.enums.Messages;
import fr.bio.apiauthentication.enums.TokenType;
import fr.bio.apiauthentication.repositories.TokenRepository;
import fr.bio.apiauthentication.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class JwtTokenUtil {
    private static final String USER = "User";

    private final UserRepository userRepository;
    private final TokenRepository tokenRepository;

    public void saveUserToken(
            UserDetails userDetails,
            String strToken,
            TokenType type
    ) {
        User user = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new UsernameNotFoundException(Messages.ENTITY_NOT_FOUND.formatMessage(USER, userDetails.getUsername())));

        Token token = Token.builder()
                .token(strToken)
                .type(type)
                .user(user)
                .build();
        tokenRepository.save(token);
    }

    public void revokeAllUserTokens(
            UserDetails userDetails,
            TokenType type
    ) {
        User user = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new UsernameNotFoundException(Messages.ENTITY_NOT_FOUND.formatMessage(USER, userDetails.getUsername())));

        List<Token> tokens = tokenRepository.findByUserAndTypeAndExpiredFalseAndRevokedFalse(user, type);

        tokens.forEach(token -> {
            token.setExpired(true);
            token.setRevoked(true);
        });

        tokenRepository.saveAll(tokens);
    }
}
