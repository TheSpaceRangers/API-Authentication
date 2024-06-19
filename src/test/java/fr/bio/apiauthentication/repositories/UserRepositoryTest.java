package fr.bio.apiauthentication.repositories;

import fr.bio.apiauthentication.entities.Role;
import fr.bio.apiauthentication.entities.User;

import jakarta.transaction.Transactional;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Collections;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@DisplayName("Test User JPA Repository")
@DataJpaTest
@Transactional
public class    UserRepositoryTest {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    private User user;
    private Role role;

    @BeforeEach
    void setUp() {
        role = Role.builder()
                .roleName("ADMIN")
                .build();
        role = roleRepository.save(role);

        user = User.builder()
                .email("c.tronel@test.com")
                .password("password")
                .firstName("firstName")
                .lastName("lastName")
                .enabled(true)
                .roles(Collections.singleton(role))
                .build();
    }

    @AfterEach
    void tearDown() {
        userRepository.delete(user);
        roleRepository.delete(role);

        user = null;
        role = null;
    }

    @Test
    @DisplayName("Test save user")
    public void testSaveUser() {
        User savedUser = userRepository.save(user);

        assertThat(savedUser).isNotNull();
        assertThat(savedUser.getEmail()).isEqualTo(user.getEmail());
        assertThat(savedUser.getRoles().size()).isEqualTo(1);
        assertThat(savedUser.getRoles().iterator().next().getRoleName()).isEqualTo("ADMIN");

        userRepository.delete(savedUser);
    }

    @Test
    @DisplayName("Test find user by email")
    public void testFindByEmail() {
        User savedUser = userRepository.save(user);

        Optional<User> foundUser = userRepository.findByEmail(savedUser.getEmail());

        assertThat(foundUser.isPresent()).isTrue();

        userRepository.delete(savedUser);
    }

    @Test
    @DisplayName("Test find user by first name and last name")
    public void testFindByFirstNameAndLastName() {
        User savedUser = userRepository.save(user);

        Optional<User> foundUser = userRepository.findByFirstNameAndLastName(savedUser.getFirstName(), savedUser.getLastName());

        assertThat(foundUser.isPresent()).isTrue();

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
