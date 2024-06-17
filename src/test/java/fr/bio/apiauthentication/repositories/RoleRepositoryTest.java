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

    private Role role;

    @BeforeEach
    void setUp() {
        role = Role.builder()
                .roleName("USER")
                .build();
    }

    @AfterEach
    void tearDown() {
        roleRepository.deleteAll();
        role = null;
    }

    @Test
    public void testSaveRole() {
        Role savedRole = roleRepository.save(role);

        assertThat(savedRole).isNotNull();
        assertThat(savedRole.getRoleName()).isEqualTo(role.getRoleName());
    }

    @Test
    public void testFindByRoleName() {
        Role savedRole = roleRepository.save(role);

        Optional<Role> foundRole = roleRepository.findByRoleName(role.getRoleName());

        assertThat(foundRole.isPresent()).isTrue();
    }

    @Test
    public void testDeleteRole() {
        Role savedRole = roleRepository.save(role);

        roleRepository.deleteById(savedRole.getIdRole());

        Optional<Role> foundRole = roleRepository.findById(role.getIdRole());

        assertThat(foundRole.isPresent()).isFalse();
    }
}
