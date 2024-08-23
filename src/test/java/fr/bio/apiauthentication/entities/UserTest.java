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
    }

    @Test
    @DisplayName("Test create user")
    public void testCreateUser() {
        final User savedUser = entityManager.persistAndFlush(user);

        assertThat(savedUser).isNotNull();
        assertThat(savedUser).isEqualTo(user);
        assertThat(savedUser.hashCode()).isEqualTo(user.hashCode());
        assertThat(savedUser).usingRecursiveComparison().isEqualTo(user);
    }

    @Test
    @DisplayName("Test update user")
    public void testUpdateUser() {
        final User savedUser = entityManager.persistAndFlush(user);

        long idUser = Long.parseLong(RandomStringUtils.randomNumeric(8));
        email = RandomStringUtils.randomAlphanumeric(5).toUpperCase();
        password = RandomStringUtils.randomAlphanumeric(30);
        firstName = RandomStringUtils.randomAlphanumeric(20);
        lastName = RandomStringUtils.randomAlphanumeric(20);
        createdAt = savedUser.getCreatedAt();
        createdBy = RandomStringUtils.randomAlphanumeric(20);
        modifiedAt = NOW.plusMonths(12);
        modifiedBy = RandomStringUtils.randomAlphanumeric(20);
        enabled = Boolean.parseBoolean(RandomStringUtils.randomNumeric(0, 1));

        savedUser.setIdUser(idUser);
        savedUser.setFirstName(firstName);
        savedUser.setLastName(lastName);
        savedUser.setEmail(email);
        savedUser.setPassword(password);
        savedUser.setCreatedAt(createdAt);
        savedUser.setCreatedBy(createdBy);
        savedUser.setModifiedAt(modifiedAt);
        savedUser.setModifiedBy(modifiedBy);

        final User updatedUser = entityManager.merge(savedUser);

        assertThat(updatedUser).isNotNull();
        assertThat(updatedUser).isEqualTo(savedUser);
        assertThat(updatedUser).usingRecursiveComparison().isEqualTo(user);
    }

    @Test
    @DisplayName("Test get granted authorities")
    public void testGetGrantedAuthorities() {
        final Role role = Role.builder()
                .authority(RandomStringUtils.randomAlphanumeric(10).toUpperCase())
                .displayName(RandomStringUtils.randomAlphanumeric(20))
                .description(RandomStringUtils.randomAlphanumeric(20))
                .modifiedAt(NOW)
                .modifiedBy(RandomStringUtils.randomAlphanumeric(20))
                .build();
        entityManager.persistAndFlush(role);
        final SimpleGrantedAuthority authority = new SimpleGrantedAuthority(role.getAuthority());
        final List<SimpleGrantedAuthority> grantedAuthorities = Collections.singletonList(authority);

        user.setRoles(Collections.singleton(role));

        final User savedUser = entityManager.persistAndFlush(user);

        assertThat(savedUser).isNotNull();
        assertThat(savedUser).isEqualTo(user);
        assertThat(savedUser).usingRecursiveComparison().isEqualTo(user);
        assertThat(savedUser.getAuthorities()).isEqualTo(grantedAuthorities);
        assertThat(savedUser.isAccountNonExpired()).isEqualTo(enabled);
        assertThat(savedUser.isAccountNonLocked()).isEqualTo(enabled);
        assertThat(savedUser.isCredentialsNonExpired()).isEqualTo(enabled);
    }

    @Test
    @DisplayName("Test same object")
    public void testEqualsAndHashCode_SameObject() {
        final User savedUser = entityManager.persistAndFlush(user);

        assertThat(savedUser).isEqualTo(user);
        assertThat(savedUser.hashCode()).isEqualTo(user.hashCode());
    }

    @Test
    @DisplayName("Test null")
    public void testEqualsAndHashCode_Null() {
        assertThat((User) null).isNotEqualTo(user);
        assertThat((User) null).isNotEqualTo(user.hashCode());
    }

    @Test
    @DisplayName("Test different class")
    public void testEqualsAndHashCode_DifferentClass() {
        assertThat(user).isNotEqualTo("This is a different object");
        assertThat(user.hashCode()).isNotEqualTo("This is a different object".hashCode());
    }

    @Test
    @DisplayName("Test different fields")
    public void testEqualsAndHashCode_DifferentFields() {
        final User differentFields = User.builder()
                .idUser(Long.parseLong(RandomStringUtils.randomNumeric(8)))
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
        assertThat(differentFields.hashCode()).isNotEqualTo(user.hashCode());
    }

    @Test
    @DisplayName("Test same fields")
    public void testEqualsAndHashCode_SameFields() {
        final User sameFields = User.builder()
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
        assertThat(sameFields.hashCode()).isEqualTo(user.hashCode());
    }

    @Test
    public void testToString() {
        final User savedUser = entityManager.persistAndFlush(user);
        final String exceptedString = "User(" +
                "idUser=" + savedUser.getIdUser() + ", " +
                "email=" + savedUser.getEmail() + ", " +
                "password=" + savedUser.getPassword() + ", " +
                "firstName=" + savedUser.getFirstName() + ", " +
                "lastName=" + savedUser.getLastName() + ", " +
                "createdAt=" + savedUser.getCreatedAt() + ", " +
                "createdBy=" + savedUser.getCreatedBy() + ", " +
                "modifiedAt=" + savedUser.getModifiedAt() + ", " +
                "modifiedBy=" + savedUser.getModifiedBy() + ", " +
                "enabled=" + savedUser.isEnabled() +
                ")";
        final String exceptedStringLombok = "User.UserBuilder(" +
                "idUser=" + savedUser.getIdUser() + ", " +
                "email=" + savedUser.getEmail() + ", " +
                "password=" + savedUser.getPassword() + ", " +
                "firstName=" + savedUser.getFirstName() + ", " +
                "lastName=" + savedUser.getLastName() + ", " +
                "createdAt=" + savedUser.getCreatedAt() + ", " +
                "createdBy=" + savedUser.getCreatedBy() + ", " +
                "modifiedAt=" + savedUser.getModifiedAt() + ", " +
                "modifiedBy=" + savedUser.getModifiedBy() + ", " +
                "enabled$value=" + savedUser.isEnabled() + ", " +
                "roles=" + savedUser.getRoles() +
                ")";

        assertThat(savedUser).isNotNull();
        assertThat(exceptedString).isEqualTo(savedUser.toString());
        assertThat(User.builder()
                .idUser(savedUser.getIdUser())
                .email(savedUser.getEmail())
                .password(savedUser.getPassword())
                .firstName(savedUser.getFirstName())
                .lastName(savedUser.getLastName())
                .createdAt(savedUser.getCreatedAt())
                .createdBy(savedUser.getCreatedBy())
                .modifiedAt(savedUser.getModifiedAt())
                .modifiedBy(savedUser.getModifiedBy())
                .enabled(savedUser.isEnabled())
                .toString()
        ).isEqualTo(exceptedStringLombok);
    }
}