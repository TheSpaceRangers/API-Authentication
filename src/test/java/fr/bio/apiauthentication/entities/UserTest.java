package fr.bio.apiauthentication.entities;

import jakarta.transaction.Transactional;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

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
        user.setEnabled(false);

        user = entityManager.persistAndFlush(user);
    }

    @Test
    @Transactional
    public void testEquals_SameObject() {
        assertThat(user).isEqualTo(user);
    }

    @Test
    @Transactional
    public void testEquals_Null() {
        assertThat(user).isNotEqualTo(null);
    }

    @Test
    @Transactional
    public void testEquals_DifferentClass() {
        assertThat(user).isNotEqualTo("This is a different object");
    }

    @Test
    @Transactional
    public void testEquals_DifferentFields() {
        User differentFields = User.builder()
                .email("c.tronel@test.com")
                .password("password")
                .firstName("Charles")
                .lastName("TRONEL")
                .enabled(true)
                .build();

        assertThat(user).isNotEqualTo(differentFields);
    }

    @Test
    @Transactional
    public void testEquals_SameFields() {
        User sameFields = User.builder()
                .email("c.tronel@test.com")
                .password("password")
                .firstName("firstName")
                .lastName("lastName")
                .enabled(true)
                .build();

        assertThat(user).isEqualTo(sameFields);
    }

    @Test
    @Transactional
    public void testHashCode_SameObject() {
        assertThat(user.hashCode()).isEqualTo(user.hashCode());
    }

    @Test
    @Transactional
    public void testHashCode_Null() {
        assertThat(user.hashCode()).isNotEqualTo(null);
    }

    @Test
    @Transactional
    public void testHashCode_DifferentClass() {
        assertThat(user.hashCode()).isNotEqualTo("This is a different object".hashCode());
    }

    @Test
    @Transactional
    public void testHashCode_DifferentFields() {
        User differentFields = User.builder()
                .email("c.tronel@test.com")
                .password("password")
                .firstName("Charles")
                .lastName("TRONEL")
                .enabled(true)
                .build();

        assertThat(user.hashCode()).isNotEqualTo(differentFields.hashCode());
    }

    @Test
    @Transactional
    public void testHashCode_SameFields() {
        User sameFields = User.builder()
                .email("c.tronel@test.com")
                .password("password")
                .firstName("firstName")
                .lastName("lastName")
                .enabled(true)
                .build();

        assertThat(user.hashCode()).isEqualTo(sameFields.hashCode());
    }
}
