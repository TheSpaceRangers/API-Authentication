package fr.bio.apiauthentication.repositories;

import fr.bio.apiauthentication.entities.Role;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

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
                .roleName("ROLE_SAVE")
                .build();
        Role savedRole = roleRepository.save(role);

        assertThat(savedRole).isNotNull();
        assertThat(savedRole.getAuthority()).isEqualTo(role.getAuthority());
    }

    @Test
    @DisplayName("Test find role by role name")
    public void testFindByRoleName() {
        Role role = Role.builder()
                .roleName("ROLE_FIND")
                .build();
        Role savedRole = roleRepository.save(role);

        Optional<Role> foundRole = roleRepository.findByRoleName(savedRole.getAuthority());

        assertThat(foundRole.isPresent()).isTrue();
    }

    @Test
    @DisplayName("Test delete role")
    public void testDeleteRole() {
        Role role = Role.builder()
                .roleName("ROLE_DELETE")
                .build();
        Role savedRole = roleRepository.save(role);

        roleRepository.deleteById(savedRole.getIdRole());

        Optional<Role> foundRole = roleRepository.findById(savedRole.getIdRole());

        assertThat(foundRole.isPresent()).isFalse();
    }
}
