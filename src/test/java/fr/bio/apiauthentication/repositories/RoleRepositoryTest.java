package fr.bio.apiauthentication.repositories;

import fr.bio.apiauthentication.entities.Role;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@DisplayName("Test Role JPA Repository")
@DataJpaTest
@Transactional
public class RoleRepositoryTest {
    @Autowired
    private RoleRepository roleRepository;

    @BeforeEach
    void setUp() {
        roleRepository.deleteAll();
    }

    @AfterEach
    void tearDown() {
        roleRepository.deleteAll();
    }

    @Test
    @DisplayName("Test save role")
    public void testSaveRole() {
        Role role = Role.builder()
                .authority("ROLE_SAVE")
                .displayName("Utilisateur")
                .description("Utilisateur")
                .users(null)
                .build();
        Role savedRole = roleRepository.save(role);

        assertThat(savedRole).isNotNull();
        assertThat(savedRole.getAuthority()).isEqualTo(role.getAuthority());
    }

    @Test
    @DisplayName("Test find role by role name")
    public void testFindByRoleName() {
        Role role = Role.builder()
                .authority("ROLE_FIND")
                .displayName("Utilisateur")
                .description("Utilisateur")
                .users(null)
                .build();
        Role savedRole = roleRepository.save(role);

        Optional<Role> foundRole = roleRepository.findByAuthority(savedRole.getAuthority());

        assertThat(foundRole.isPresent()).isTrue();
    }

    @Test
    @DisplayName("Test find all role by is enable")
    public void testFindAllByIsEnable() {
        Role roleEnable = Role.builder()
                .authority("ROLE_ENABLE")
                .displayName("Utilisateur")
                .description("Utilisateur")
                .enabled(true)
                .users(null)
                .build();
        roleRepository.save(roleEnable);

        Role roleDesable = Role.builder()
                .authority("ROLE_DISABLE")
                .displayName("Utilisateur")
                .description("Utilisateur")
                .enabled(false)
                .users(null)
                .build();
        roleRepository.save(roleDesable);

        List<Role> actualRoles = List.of(roleEnable);
        List<Role> foundRoles = roleRepository.findAllByEnabled(true);

        assertThat(foundRoles).isEqualTo(actualRoles);
    }

    @Test
    @DisplayName("Test delete role")
    public void testDeleteRole() {
        Role role = Role.builder()
                .authority("ROLE_DELETE")
                .displayName("Utilisateur")
                .description("Utilisateur")
                .users(null)
                .build();
        Role savedRole = roleRepository.save(role);

        roleRepository.deleteById(savedRole.getIdRole());

        Optional<Role> foundRole = roleRepository.findById(savedRole.getIdRole());

        assertThat(foundRole.isPresent()).isFalse();
    }
}
