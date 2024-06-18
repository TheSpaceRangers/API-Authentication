package fr.bio.apiauthentication.repositories;

import fr.bio.apiauthentication.entities.Token;
import fr.bio.apiauthentication.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface TokenRepository extends JpaRepository<Token, Long> {
    Optional<Token> findByToken(String token);

    List<Token> findAllByUser_IdUser(Long idUser);

    List<Token> findAllByUser_Email(String email);

    @Query(value = """
        select t from Token t inner join User u
        on t.user.idUser = u.idUser
        where u.idUser = :idUser and (t.expired = false and t.revoked = false )
    """)
    List<Token> findAllValidTokenByUser(User user);
}
