package fr.bio.apiauthentication.repositories;

import fr.bio.apiauthentication.entities.Token;
import fr.bio.apiauthentication.entities.User;
import fr.bio.apiauthentication.enums.TokenType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TokenRepository extends JpaRepository<Token, Long> {
    Optional<Token> findByToken(String token);

    List<Token> findAllByUser_IdUser(Long idUser);

    List<Token> findAllByUser_Email(String email);

    List<Token> findByUserAndTypeAndExpiredFalseAndRevokedFalse(User user, TokenType type);

    List<Token> findByUserAndTypeAndExpiredTrueAndRevokedTrue(User user, TokenType type);

    void deleteByUserAndType(User user, TokenType type);
}