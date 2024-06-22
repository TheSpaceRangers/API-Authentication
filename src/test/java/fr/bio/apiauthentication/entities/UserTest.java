package fr.bio.apiauthentication.entities;

import jakarta.transaction.Transactional;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@DisplayName("Test User JPA Entity")
@DataJpaTest
@Transactional
public class UserTest {
    @Autowired
    private TestEntityManager entityManager;

    private User user;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .email("c.tronel@test.properties.com")
                .password("password")
                .firstName("firstName")
                .lastName("lastName")
                .createdAt(LocalDate.now())
                .createdBy("System")
                .modifiedAt(LocalDate.now())
                .modifiedBy("System")
                .enabled(true)
                .build();
    }

    @AfterEach
    void tearDown() {
        user = null;
    }

    @Test
    @DisplayName("Test create user")
    public void testCreateUser() {
        user = entityManager.persistAndFlush(user);
    }

    @Test
    @DisplayName("Test update user")
    public void testUpdateUser() {
        Role role = Role.builder()
                .roleName("UPDATE_USER")
                .build();
        entityManager.persist(role);

        user.setFirstName("John");
        user.setLastName("Doe");
        user.setEmail("j.doe@test.properties.com");
        user.setPassword("password 3");
        user.setCreatedAt(LocalDate.now());
        user.setCreatedBy("System");
        user.setModifiedAt(LocalDate.now());
        user.setRoles(Collections.singleton(role));
        user.setModifiedBy("System");

        user.setEnabled(false);

        user = entityManager.persistAndFlush(user);
    }

    @Test
    @DisplayName("Test user details")
    public void testUserDetails() {
        Role role = Role.builder()
                .roleName("USER_DETAILS")
                .build();
        entityManager.persist(role);

        SimpleGrantedAuthority authority = new SimpleGrantedAuthority(role.getRoleName());

        user.setRoles(Collections.singleton(role));
        User persitedUser = entityManager.persist(user);

        assertThat(persitedUser).isNotNull();
        assertThat(persitedUser.getEmail()).isEqualTo(user.getUsername());
        assertThat(persitedUser.isAccountNonExpired()).isTrue();
        assertThat(persitedUser.isAccountNonLocked()).isTrue();
        assertThat(persitedUser.isCredentialsNonExpired()).isTrue();
        assertThat(persitedUser.getAuthorities()).isEqualTo(List.of(authority));
    }

    @Test
    @DisplayName("Test same object")
    public void testEquals_SameObject() {
        assertThat(user).isEqualTo(user);
    }

    @Test
    @DisplayName("Test null")
    public void testEquals_Null() {
        assertThat(user).isNotEqualTo(null);
    }

    @Test
    @DisplayName("Test different class")
    public void testEquals_DifferentClass() {
        assertThat(user).isNotEqualTo("This is a different object");
    }

    @Test
    @DisplayName("Test different fields")
    public void testEquals_DifferentFields() {
        User differentFields = User.builder()
                .email("c.tronel@test.properties.com")
                .password("password")
                .firstName("Charles")
                .lastName("TRONEL")
                .enabled(true)
                .build();

        assertThat(user).isNotEqualTo(differentFields);
    }

    @Test
    @DisplayName("Test same fields")
    public void testEquals_SameFields() {
        User sameFields = User.builder()
                .email("c.tronel@test.properties.com")
                .password("password")
                .firstName("firstName")
                .lastName("lastName")
                .createdAt(LocalDate.now())
                .createdBy("System")
                .modifiedAt(LocalDate.now())
                .modifiedBy("System")
                .enabled(true)
                .build();

        assertThat(user).isEqualTo(sameFields);
    }

    @Test
    @DisplayName("Test same fields hashCode")
    public void testHashCode_SameFields() {
        User sameFields = User.builder()
                .email("c.tronel@test.properties.com")
                .password("password")
                .firstName("firstName")
                .lastName("lastName")
                .createdAt(LocalDate.now())
                .createdBy("System")
                .modifiedAt(LocalDate.now())
                .modifiedBy("System")
                .enabled(true)
                .build();

        assertThat(user.hashCode()).isEqualTo(sameFields.hashCode());
    }
}
