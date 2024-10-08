package fr.bio.apiauthentication.repositories;

import fr.bio.apiauthentication.entities.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByAuthority(String authority);

    List<Role> findAllByEnabled(boolean enabled);

    List<Role> findAllByAuthorityIn(List<String> authority);
}