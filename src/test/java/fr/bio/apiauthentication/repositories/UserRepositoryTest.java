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

@DisplayName("Test User JPA Repository")
@DataJpaTest
@Transactional
public class    UserRepositoryTest {
    private static final LocalDate NOW = LocalDate.now();

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    private User user;
    private Role role;

    private String email;
    private String password;
    private String firstName;
    private String lastName;
    private LocalDate createdAt;
    private String createdBy;
    private LocalDate modifiedAt;
    private String modifiedBy;
    private boolean enabled;

    @BeforeEach
    void setUp() {
        email = RandomStringUtils.randomAlphanumeric(5).toUpperCase();
        password = RandomStringUtils.randomAlphanumeric(30);
        firstName = RandomStringUtils.randomAlphanumeric(20);
        lastName = RandomStringUtils.randomAlphanumeric(20);
        createdAt = NOW;
        createdBy = RandomStringUtils.randomAlphanumeric(20);
        modifiedAt = NOW;
        modifiedBy = RandomStringUtils.randomAlphanumeric(20);
        enabled = Boolean.parseBoolean(RandomStringUtils.randomNumeric(0, 1));

        role = Role.builder()
                .authority(RandomStringUtils.randomAlphanumeric(10).toUpperCase())
                .displayName(RandomStringUtils.randomAlphanumeric(20))
                .description(RandomStringUtils.randomAlphanumeric(20))
                .modifiedAt(NOW)
                .modifiedBy(RandomStringUtils.randomAlphanumeric(20))
                .build();
        roleRepository.save(role);

        user = User.builder()
                .email(email)
                .password(password)
                .firstName(firstName)
                .lastName(lastName)
                .createdAt(createdAt)
                .createdBy(createdBy)
                .modifiedAt(modifiedAt)
                .modifiedBy(modifiedBy)
                .enabled(enabled)
                .roles(List.of(role))
                .build();
    }

    @AfterEach
    void tearDown() {
        userRepository.deleteAll();
        roleRepository.deleteAll();

        user = null;
        role = null;
    }

    @Test
    @DisplayName("Test save user")
    public void testSaveUser() {
        User savedUser = userRepository.save(user);

        assertThat(savedUser).isNotNull();
        assertThat(savedUser.getEmail()).isEqualTo(email);
        assertThat(savedUser.getPassword()).isEqualTo(password);
        assertThat(savedUser.getFirstName()).isEqualTo(firstName);
        assertThat(savedUser.getLastName()).isEqualTo(lastName);
        assertThat(savedUser.getCreatedAt()).isEqualTo(createdAt);
        assertThat(savedUser.getCreatedBy()).isEqualTo(createdBy);
        assertThat(savedUser.getModifiedAt()).isEqualTo(modifiedAt);
        assertThat(savedUser.getModifiedBy()).isEqualTo(modifiedBy);
        assertThat(savedUser.isEnabled()).isEqualTo(enabled);
    }

    @Test
    @DisplayName("Test find user by email")
    public void testFindByEmail() {
        User savedUser = userRepository.save(user);

        Optional<User> exceptedUser = Optional.of(savedUser);
        Optional<User> foundUser = userRepository.findByEmail(savedUser.getEmail());

        assertThat(foundUser.isPresent()).isTrue();
        assertThat(foundUser).isEqualTo(exceptedUser);
    }

    @Test
    @DisplayName("Test find user by first name and last name")
    public void testFindByFirstNameAndLastName() {
        User savedUser = userRepository.save(user);

        Optional<User> exceptedUser = Optional.of(savedUser);
        Optional<User> foundUser = userRepository.findByFirstNameAndLastName(savedUser.getFirstName(), savedUser.getLastName());

        assertThat(foundUser.isPresent()).isTrue();
        assertThat(foundUser).isEqualTo(exceptedUser);

        userRepository.delete(savedUser);
    }

    @Test
    @DisplayName("Test delete user by ID")
    public void testDeleteById() {
        User savedUser = userRepository.save(user);

        userRepository.deleteById(savedUser.getIdUser());

        Optional<User> deletedUser = userRepository.findById(savedUser.getIdUser());

        assertThat(deletedUser.isPresent()).isFalse();

        userRepository.delete(savedUser);
    }
}