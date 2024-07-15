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

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@DisplayName("Test Role JPA Entity")
@DataJpaTest
@Transactional
public class RoleTest {
    private static final LocalDate NOW = LocalDate.now();

    @Autowired
    private TestEntityManager entityManager;

    private Role role;
    private User user;

    private String authority;
    private String displayName;
    private String description;
    private LocalDate modifiedAt;
    private String modifiedBy;
    private boolean enabled;

    @BeforeEach
    void setUp() {
        authority = RandomStringUtils.randomAlphanumeric(20).toUpperCase();
        displayName = RandomStringUtils.randomAlphanumeric(20);
        description = RandomStringUtils.randomAlphanumeric(20);
        modifiedAt = NOW;
        modifiedBy = RandomStringUtils.randomAlphanumeric(20);
        enabled = Boolean.parseBoolean(RandomStringUtils.randomNumeric(0, 1));

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

        role = Role.builder()
                .authority(authority)
                .displayName(displayName)
                .description(description)
                .modifiedAt(modifiedAt)
                .modifiedBy(modifiedBy)
                .enabled(enabled)
                .users(new ArrayList<>(List.of(user)))
                .build();
    }

    @AfterEach
    void tearDown() {
        role = null;
    }

    @Test
    @DisplayName("Test create role")
    public void testCreateRole() {
        final Role savedRole = entityManager.persistAndFlush(role);

        assertThat(savedRole).isNotNull();
        assertThat(savedRole).isEqualTo(role);
        assertThat(savedRole.hashCode()).isEqualTo(role.hashCode());
        assertThat(savedRole).usingRecursiveComparison().isEqualTo(role);
    }

    @Test
    @DisplayName("Test update role")
    public void testUpdateRole() {
        final Role savedRole = entityManager.persistAndFlush(role);

        long idRole = Long.parseLong(RandomStringUtils.randomNumeric(8));
        authority = RandomStringUtils.randomAlphanumeric(10).toUpperCase();
        displayName = RandomStringUtils.randomAlphanumeric(20);
        description = RandomStringUtils.randomAlphanumeric(20);
        modifiedAt = NOW.plusDays(10);
        modifiedBy = RandomStringUtils.randomAlphanumeric(20);

        savedRole.setIdRole(idRole);
        savedRole.setAuthority(authority);
        savedRole.setDisplayName(displayName);
        savedRole.setDescription(description);
        savedRole.setModifiedAt(modifiedAt);
        savedRole.setModifiedBy(modifiedBy);

        final Role updatedRole = entityManager.merge(savedRole);

        assertThat(updatedRole).isNotNull();
        assertThat(updatedRole).isEqualTo(savedRole);
        assertThat(updatedRole).usingRecursiveComparison().isEqualTo(savedRole);
    }

    @Test
    @DisplayName("Test same object")
    public void testEqualsAndHashCode_SameObject() {
        final Role sameRole = role;

        assertThat(sameRole).isEqualTo(role);
        assertThat(sameRole.hashCode()).isEqualTo(role.hashCode());
    }

    @Test
    @DisplayName("Test null")
    public void testEqualsAndHashCode_Null() {
        assertThat((Role) null).isNotEqualTo(role);
        assertThat((Role) null).isNotEqualTo(role.hashCode());
    }

    @Test
    @DisplayName("Test different class")
    public void testEqualsAndHashCode_DifferentClass() {
        assertThat(role).isNotEqualTo("This is a different object");
        assertThat(role.hashCode()).isNotEqualTo("This is a different object".hashCode());
    }

    @Test
    @DisplayName("Test different fields")
    public void testEqualsAndHashCode_DifferentFields() {
        final Role differentFields = Role.builder()
                .authority(RandomStringUtils.randomAlphanumeric(10).toUpperCase())
                .displayName(RandomStringUtils.randomAlphanumeric(20))
                .description(RandomStringUtils.randomAlphanumeric(20))
                .modifiedAt(NOW.plusMonths(10))
                .modifiedBy(RandomStringUtils.randomAlphanumeric(20))
                .build();

        assertThat(differentFields).isNotEqualTo(role);
        assertThat(differentFields.hashCode()).isNotEqualTo(role.hashCode());
    }

    @Test
    @DisplayName("Test same fields")
    public void testEqualsAndHashCode_SameFields() {
        final Role sameFields = Role.builder()
                .authority(authority)
                .displayName(displayName)
                .description(description)
                .modifiedAt(modifiedAt)
                .modifiedBy(modifiedBy)
                .enabled(enabled)
                .users(List.of(user))
                .build();

        assertThat(sameFields).isEqualTo(role);
        assertThat(sameFields.hashCode()).isEqualTo(role.hashCode());
    }

    @Test
    @DisplayName("Test same fields hashCode")
    public void testHashCode_SameFields() {
        final Role sameFields = Role.builder()
                .authority(authority)
                .displayName(displayName)
                .description(description)
                .modifiedAt(modifiedAt)
                .modifiedBy(modifiedBy)
                .enabled(enabled)
                .users(List.of(user))
                .build();

        assertThat(sameFields.hashCode()).isEqualTo(role.hashCode());
    }

    @Test
    public void testToString() {
        final Role savedRole = entityManager.persistAndFlush(role );
        final String exceptedString = "Role(" +
                "idRole=" + savedRole.getIdRole() + ", " +
                "authority=" + savedRole.getAuthority() + ", " +
                "displayName=" + savedRole.getDisplayName() + ", " +
                "description=" + savedRole.getDescription() + ", " +
                "modifiedAt=" + savedRole.getModifiedAt() + ", " +
                "modifiedBy=" + savedRole.getModifiedBy() + ", " +
                "enabled=" + savedRole.isEnabled() +
                ")";
        final String exceptedStringLombok = "Role.RoleBuilder(" +
                "idRole=" + savedRole.getIdRole() + ", " +
                "authority=" + savedRole.getAuthority() + ", " +
                "displayName=" + savedRole.getDisplayName() + ", " +
                "description=" + savedRole.getDescription() + ", " +
                "modifiedAt=" + savedRole.getModifiedAt() + ", " +
                "modifiedBy=" + savedRole.getModifiedBy() + ", " +
                "enabled$value=" + savedRole.isEnabled() + ", " +
                "users=" + savedRole.getUsers() +
                ")";

        assertThat(savedRole).isNotNull();
        assertThat(exceptedString).isEqualTo(savedRole.toString());
        assertThat(Role.builder()
                .idRole(savedRole.getIdRole())
                .authority(savedRole.getAuthority())
                .displayName(savedRole.getDisplayName())
                .description(savedRole.getDescription())
                .modifiedAt(savedRole.getModifiedAt())
                .modifiedBy(savedRole.getModifiedBy())
                .enabled(savedRole.isEnabled())
                .users(savedRole.getUsers())
                .toString()
        ).isEqualTo(exceptedStringLombok);
    }
}