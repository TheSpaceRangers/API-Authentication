package fr.bio.apiauthentication.repositories;

import fr.bio.apiauthentication.entities.LoginHistory;
import fr.bio.apiauthentication.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LoginHistoryRepository extends JpaRepository<LoginHistory, Long> {
    List<LoginHistory> findAllByUser(User user);
}