package fr.bio.apiauthentication.repositories;

import fr.bio.apiauthentication.entities.Role;
import fr.bio.apiauthentication.entities.User;
import jakarta.transaction.Transactional;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@DisplayName("Test Role JPA Repository")
@DataJpaTest
@Transactional
public class RoleRepositoryTest {
    private static final LocalDate NOW = LocalDate.now();

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private UserRepository userRepository;

    private Role role;
    private User user;

    @BeforeEach
    void setUp() {
        final String authority = RandomStringUtils.randomAlphanumeric(20).toUpperCase();
        final String displayName = RandomStringUtils.randomAlphanumeric(20);
        final String description = RandomStringUtils.randomAlphanumeric(20);
        final String modifiedBy = RandomStringUtils.randomAlphanumeric(20);
        final boolean enabled = Boolean.parseBoolean(RandomStringUtils.randomNumeric(0, 1));

        user = User.builder()
                .email(RandomStringUtils.randomAlphanumeric(10) + "@test.com")
                .password(RandomStringUtils.randomAlphanumeric(30))
                .firstName(RandomStringUtils.randomAlphanumeric(20))
                .lastName(RandomStringUtils.randomAlphanumeric(20))
                .createdAt(NOW)
                .createdBy(RandomStringUtils.randomAlphanumeric(20))
                .modifiedAt(NOW)
                .modifiedBy(RandomStringUtils.randomAlphanumeric(20))
                .enabled(true)
                .build();
        user = userRepository.save(user);

        role = Role.builder()
                .authority(authority)
                .displayName(displayName)
                .description(description)
                .modifiedAt(NOW)
                .modifiedBy(modifiedBy)
                .enabled(enabled)
                .users(List.of(user))
                .build();
    }

    @AfterEach
    void tearDown() {
        roleRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    @DisplayName("Test save role")
    public void testSaveRole() {
        Role savedRole = roleRepository.save(role);

        assertThat(savedRole).isNotNull();
        assertThat(savedRole).isEqualTo(role);
        assertThat(savedRole).usingRecursiveComparison().isEqualTo(role);
    }

    @Test
    @DisplayName("Test find all roles")
    public void testFindAll() {
        Role savedRole_1 = roleRepository.save(role);
        Role savedRole_2 = roleRepository.save(generateRole(true));

        List<Role> expectedRoles = List.of(savedRole_1, savedRole_2);
        List<Role> foundRoles = roleRepository.findAll();

        assertThat(foundRoles).isEqualTo(expectedRoles);
    }

    @Test
    @DisplayName("Test find role by role name")
    public void testFindByRoleName() {
        Role savedRole = roleRepository.save(role);

        Optional<Role> exceptedRole = Optional.of(savedRole);
        Optional<Role> foundRole = roleRepository.findByAuthority(savedRole.getAuthority());

        if (foundRole.isPresent()) {
            assertThat(foundRole).isEqualTo(exceptedRole);
            assertThat(foundRole.get()).isEqualTo(exceptedRole.get());
            assertThat(foundRole.get()).usingRecursiveComparison().isEqualTo(exceptedRole.get());
        }
    }

    @Test
    @DisplayName("Test find all role by is enable")
    public void testFindAllByIsEnable() {
        Role savedRole_1 = roleRepository.save(generateRole(true));
        Role savedRole_2 = roleRepository.save(generateRole(false));

        List<Role> exceptedRolesEnabled = List.of(savedRole_1);
        List<Role> exceptedRolesDisabled = List.of(savedRole_2);

        List<Role> foundRolesEnabled = roleRepository.findAllByEnabled(true);
        List<Role> foundRolesDisabled = roleRepository.findAllByEnabled(false);

        assertThat(foundRolesEnabled).isEqualTo(exceptedRolesEnabled);
        assertThat(foundRolesEnabled).usingRecursiveComparison().isEqualTo(exceptedRolesEnabled);

        assertThat(foundRolesDisabled).isEqualTo(exceptedRolesDisabled);
        assertThat(foundRolesDisabled).usingRecursiveComparison().isEqualTo(exceptedRolesDisabled);
    }

    @Test
    @DisplayName("Test delete role")
    public void testDeleteRole() {
        Role savedRole = roleRepository.save(role);

        roleRepository.deleteById(savedRole.getIdRole());

        Optional<Role> foundRole = roleRepository.findById(savedRole.getIdRole());

        assertThat(foundRole.isPresent()).isFalse();
    }

    private Role generateRole(boolean enabled) {
        return Role.builder()
                .authority(RandomStringUtils.randomAlphanumeric(10).toUpperCase())
                .displayName(RandomStringUtils.randomAlphanumeric(20))
                .description(RandomStringUtils.randomAlphanumeric(20))
                .modifiedAt(NOW.plusMonths(10))
                .modifiedBy(RandomStringUtils.randomAlphanumeric(20))
                .enabled(enabled)
                .users(List.of(user))
                .build();
    }
}