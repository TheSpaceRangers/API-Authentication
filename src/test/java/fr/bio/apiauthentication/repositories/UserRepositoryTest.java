package fr.bio.apiauthentication.repositories;

import fr.bio.apiauthentication.entities.User;

import jakarta.transaction.Transactional;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@DisplayName("Test User JPA Repository")
@DataJpaTest
@Transactional
public class UserRepositoryTest {
    @Autowired
    private UserRepository userRepository;

    private User user;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .email("c.tronel@test.com")
                .password("password")
                .firstName("firstName")
                .lastName("lastName")
                .enabled(true)
                .build();
    }

    @AfterEach
    void tearDown() {
        userRepository.deleteAll();
        user = null;
    }

    @Test
    public void testSaveUser() {
        User savedUser = userRepository.save(user);

        assertThat(savedUser).isNotNull();
        assertThat(savedUser.getEmail()).isEqualTo(user.getEmail());
    }

    @Test
    public void testFindById() {
        User savedUser = userRepository.save(user);

        Optional<User> foundUser = userRepository.findById(savedUser.getIdUser());

        assertThat(foundUser.isPresent()).isTrue();
        assertThat(foundUser.get().getIdUser()).isEqualTo(user.getIdUser());
    }

    @Test
    public void testFindByEmail() {
        User savedUser = userRepository.save(user);

        Optional<User> foundUser = userRepository.findByEmail(savedUser.getEmail());

        assertThat(foundUser.isPresent()).isTrue();
    }

    @Test
    public void testFindByFirstNameAndLastName() {
        User savedUser = userRepository.save(user);

        Optional<User> foundUser = userRepository.findByFirstNameAndLastName(savedUser.getFirstName(), savedUser.getLastName());

        assertThat(foundUser.isPresent()).isTrue();
    }

    @Test
    public void testDeleteById() {
        User savedUser = userRepository.save(user);

        userRepository.deleteById(savedUser.getIdUser());

        Optional<User> deletedUser = userRepository.findById(savedUser.getIdUser());

        assertThat(deletedUser.isPresent()).isFalse();
    }
}
