package fr.bio.apiauthentication.entities;

import jakarta.transaction.Transactional;

import org.apache.commons.lang3.RandomStringUtils;
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
    private static final LocalDate NOW = LocalDate.now();

    @Autowired
    private TestEntityManager entityManager;

    private User user;

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
                .build();
    }

    @AfterEach
    void tearDown() {
        user = null;

        entityManager.flush();
    }

    @Test
    @DisplayName("Test create user")
    public void testCreateUser() {
        User savedUser = entityManager.persist(user);

        assertThat(savedUser).isNotNull();
        assertThat(savedUser).isEqualTo(user);
        assertThat(savedUser).usingRecursiveComparison().isEqualTo(user);
    }

    @Test
    @DisplayName("Test update user")
    public void testUpdateUser() {
        User savedUser = entityManager.persist(user);

        email = RandomStringUtils.randomAlphanumeric(5).toUpperCase();
        password = RandomStringUtils.randomAlphanumeric(30);
        firstName = RandomStringUtils.randomAlphanumeric(20);
        lastName = RandomStringUtils.randomAlphanumeric(20);
        modifiedAt = NOW.plusMonths(12);
        modifiedBy = RandomStringUtils.randomAlphanumeric(20);
        enabled = Boolean.parseBoolean(RandomStringUtils.randomNumeric(0, 1));

        savedUser.setFirstName(firstName);
        savedUser.setLastName(lastName);
        savedUser.setEmail(email);
        savedUser.setPassword(password);
        savedUser.setModifiedAt(modifiedAt);
        savedUser.setModifiedBy(modifiedBy);

        User updatedUser = entityManager.merge(savedUser);

        assertThat(updatedUser).isNotNull();
        assertThat(updatedUser).isEqualTo(savedUser);
        assertThat(savedUser).usingRecursiveComparison().isNotEqualTo(updatedUser);
    }

    @Test
    @DisplayName("Test get granted authorities")
    public void testGetGrantedAuthorities() {
        Role role = Role.builder()
                .authority(RandomStringUtils.randomAlphanumeric(10).toUpperCase())
                .displayName(RandomStringUtils.randomAlphanumeric(20))
                .description(RandomStringUtils.randomAlphanumeric(20))
                .modifiedAt(NOW)
                .modifiedBy(RandomStringUtils.randomAlphanumeric(20))
                .build();
        entityManager.persist(role);
        SimpleGrantedAuthority authority = new SimpleGrantedAuthority(role.getAuthority());

        user.setRoles(Collections.singleton(role));

        User savedUser = entityManager.persist(user);

        assertThat(savedUser).isNotNull();
        assertThat(savedUser).usingRecursiveComparison().isEqualTo(user);
        assertThat(savedUser.getUsername()).isEqualTo(email);
        assertThat(savedUser.isAccountNonExpired()).isEqualTo(enabled);
        assertThat(savedUser.isAccountNonLocked()).isEqualTo(enabled);
        assertThat(savedUser.isCredentialsNonExpired()).isEqualTo(enabled);
        assertThat(savedUser.getAuthorities()).isEqualTo(List.of(authority));
    }

    @Test
    @DisplayName("Test same object")
    public void testEquals_SameObject() {
        User savedUser = entityManager.persist(user);

        assertThat(savedUser).isEqualTo(user);
    }

    @Test
    @DisplayName("Test null")
    public void testEquals_Null() {
        assertThat((User) null).isNotEqualTo(user);
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
                .email(RandomStringUtils.randomAlphanumeric(5).toUpperCase())
                .password(RandomStringUtils.randomAlphanumeric(30))
                .firstName(RandomStringUtils.randomAlphanumeric(20))
                .lastName(RandomStringUtils.randomAlphanumeric(20))
                .createdAt(NOW.plusMonths(12))
                .createdBy(RandomStringUtils.randomAlphanumeric(20))
                .modifiedAt(NOW.plusMonths(12))
                .modifiedBy(RandomStringUtils.randomAlphanumeric(20))
                .enabled(Boolean.parseBoolean(RandomStringUtils.randomNumeric(0, 1)))
                .build();

        assertThat(differentFields).isNotEqualTo(user);
    }

    @Test
    @DisplayName("Test same fields")
    public void testEquals_SameFields() {
        User sameFields = User.builder()
                .email(email)
                .password(password)
                .firstName(firstName)
                .lastName(lastName)
                .createdAt(createdAt)
                .createdBy(createdBy)
                .modifiedAt(modifiedAt)
                .modifiedBy(modifiedBy)
                .enabled(enabled)
                .build();

        assertThat(sameFields).isEqualTo(user);
    }

    @Test
    @DisplayName("Test same fields hashCode")
    public void testHashCode_SameFields() {
        User sameFields = User.builder()
                .email(email)
                .password(password)
                .firstName(firstName)
                .lastName(lastName)
                .createdAt(createdAt)
                .createdBy(createdBy)
                .modifiedAt(modifiedAt)
                .modifiedBy(modifiedBy)
                .enabled(enabled)
                .build();

        assertThat(sameFields.hashCode()).isEqualTo(user.hashCode());
    }
}