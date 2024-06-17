package fr.bio.apiauthentication.entities;

import jakarta.transaction.Transactional;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("Test User JPA Entity")
@DataJpaTest
public class UserTest {
    @Autowired
    private TestEntityManager entityManager;

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
        user = null;
    }

    @Test
    @Transactional
    public void testCreateUser() {
        user = entityManager.persistAndFlush(user);
    }

    @Test
    @Transactional
    public void testUpdateUser() {
        //user.setIdUser(12345);
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setEmail("j.doe@test.com");
        user.setPassword("password 3");

        user = entityManager.persistAndFlush(user);
    }

    @Test
    @Transactional
    public void testEquals() {
        User userEquals = User.builder()
                .email("c.tronel@test.com")
                .password("password")
                .firstName("firstName")
                .lastName("lastName")
                .enabled(true)
                .build();

        assertEquals(user, userEquals);
    }

    @Test
    @Transactional
    public void testHashCode() {
        User userEquals = User.builder()
                .email("c.tronel@test.com")
                .password("password")
                .firstName("firstName")
                .lastName("lastName")
                .enabled(true)
                .build();

        assertEquals(user.hashCode(), userEquals.hashCode());
    }
}
