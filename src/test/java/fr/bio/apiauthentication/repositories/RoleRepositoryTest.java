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
import java.util.stream.IntStream;

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

    private User user;

    @BeforeEach
    void setUp() {
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
    }

    @AfterEach
    void tearDown() {
        roleRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    @DisplayName("Test save role")
    public void testSaveRole() {
        final Role savedRole = generateRole(true);

        assertThat(savedRole).isNotNull();
    }

    @Test
    @DisplayName("Test find all roles")
    public void testFindAll() {
        final Role savedRole_1 = generateRole(true);
        final Role savedRole_2 = generateRole(true);

        final List<Role> expectedRoles = List.of(savedRole_1, savedRole_2);
        final List<Role> foundRoles = roleRepository.findAll();

        assertThat(foundRoles).isEqualTo(expectedRoles);
    }

    @Test
    @DisplayName("Test find role by role name")
    public void testFindByRoleName() {
        final Role savedRole = generateRole(true);

        final Optional<Role> exceptedRole = Optional.of(savedRole);
        final Optional<Role> foundRole = roleRepository.findByAuthority(savedRole.getAuthority());

        if (foundRole.isPresent()) {
            assertThat(foundRole).isEqualTo(exceptedRole);
            assertThat(foundRole.get()).isEqualTo(exceptedRole.get());
            assertThat(foundRole.get()).usingRecursiveComparison().isEqualTo(exceptedRole.get());
        }
    }

    @Test
    @DisplayName("Test find all role by is enable")
    public void testFindAllByIsEnable() {
        final Role savedRole_Active = generateRole(true);
        final List<Role> exceptedRoles_Active = List.of(savedRole_Active);

        final Role savedRole_Inactive = generateRole(false);
        final List<Role> exceptedRoles_Inactive = List.of(savedRole_Inactive);

        final List<Role> foundRoles_Active = roleRepository.findAllByEnabled(true);
        final List<Role> foundRoles_Inactive = roleRepository.findAllByEnabled(false);

        assertThat(foundRoles_Active).isEqualTo(exceptedRoles_Active);
        assertThat(foundRoles_Active).usingRecursiveComparison().isEqualTo(exceptedRoles_Active);

        assertThat(foundRoles_Inactive).isEqualTo(exceptedRoles_Inactive);
        assertThat(foundRoles_Inactive).usingRecursiveComparison().isEqualTo(exceptedRoles_Inactive);
    }

    @Test
    @DisplayName("Test find all role by authority in")
    public void testFindAllByAuthorityIn() {
        final List<Role> expectedRoles = IntStream.range(0, 5)
                .mapToObj(role -> generateRole(true))
                .toList();
        final List<String> exceptedRolesString = expectedRoles.stream()
                .map(Role::getAuthority)
                .toList();

        final List<Role> foundRoles = roleRepository.findAllByAuthorityIn(exceptedRolesString);

        assertThat(foundRoles).isNotNull();
        assertThat(foundRoles.size()).isEqualTo(expectedRoles.size());

        for (Role foundRole : foundRoles) {
            Role expectedRole = expectedRoles.stream()
                    .filter(role -> role.getIdRole() == foundRole.getIdRole())
                    .findFirst()
                    .orElse(null);

            assertThat(expectedRole).isNotNull();

            if (expectedRole != null) {
                assertThat(foundRole.getAuthority()).isEqualTo(expectedRole.getAuthority());
                assertThat(foundRole.getDisplayName()).isEqualTo(expectedRole.getDisplayName());
                assertThat(foundRole.getDescription()).isEqualTo(expectedRole.getDescription());
                assertThat(foundRole.getModifiedAt()).isEqualTo(expectedRole.getModifiedAt());
                assertThat(foundRole.getModifiedBy()).isEqualTo(expectedRole.getModifiedBy());
                assertThat(foundRole.isEnabled()).isEqualTo(expectedRole.isEnabled());
            }
        }
    }

    @Test
    @DisplayName("Test delete role")
    public void testDeleteRole() {
        final Role savedRole = generateRole(true);

        roleRepository.deleteById(savedRole.getIdRole());

        final Optional<Role> foundRole = roleRepository.findById(savedRole.getIdRole());

        assertThat(foundRole.isPresent()).isFalse();
    }

    private Role generateRole(boolean enabled) {
        final Role role =  Role.builder()
                .authority(RandomStringUtils.randomAlphanumeric(10).toUpperCase())
                .displayName(RandomStringUtils.randomAlphanumeric(20))
                .description(RandomStringUtils.randomAlphanumeric(20))
                .modifiedAt(NOW.plusMonths(10))
                .modifiedBy(RandomStringUtils.randomAlphanumeric(20))
                .enabled(enabled)
                .users(List.of(user))
                .build();
        return roleRepository.save(role);
    }
}