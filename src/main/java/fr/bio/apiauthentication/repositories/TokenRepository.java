package fr.bio.apiauthentication.repositories;

import fr.bio.apiauthentication.entities.Token;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TokenRepository extends JpaRepository<Token, Long> {
    Optional<Token> findByToken(String token);

    List<Token> findAllByUser_IdUser(Long idUser);

    List<Token> findAllByUser_Email(String email);
}
