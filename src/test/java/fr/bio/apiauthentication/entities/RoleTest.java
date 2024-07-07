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
                .users(List.of(user))
                .build();
    }

    @AfterEach
    void tearDown() {
        entityManager.flush();
        role = null;
    }

    @Test
    @DisplayName("Test create role")
    public void testCreateRole() {
        Role persistedRole = entityManager.persist(role);

        assertThat(persistedRole).isNotNull();
        assertThat(persistedRole).isEqualTo(role);
    }

    @Test
    @DisplayName("Test update role")
    public void testUpdateRole() {
        Role persistedRole = entityManager.persist(role);

        authority = RandomStringUtils.randomAlphanumeric(10).toUpperCase();
        displayName = RandomStringUtils.randomAlphanumeric(20);
        description = RandomStringUtils.randomAlphanumeric(20);
        modifiedAt = NOW.plusDays(10);
        modifiedBy = RandomStringUtils.randomAlphanumeric(20);

        persistedRole.setAuthority(authority);
        persistedRole.setDisplayName(displayName);
        persistedRole.setDescription(description);
        persistedRole.setModifiedAt(modifiedAt);
        persistedRole.setModifiedBy(modifiedBy);

        Role updatedRole = entityManager.merge(persistedRole);

        assertThat(updatedRole).isNotNull();
        assertThat(updatedRole).isNotEqualTo(persistedRole);
        assertThat(updatedRole.getAuthority()).isEqualTo(authority);
        assertThat(updatedRole.getDisplayName()).isEqualTo(displayName);
        assertThat(updatedRole.getDescription()).isEqualTo(description);
        assertThat(updatedRole.getModifiedAt()).isEqualTo(modifiedAt);
        assertThat(updatedRole.getModifiedBy()).isEqualTo(modifiedBy);
    }

    @Test
    @DisplayName("Test same object")
    public void testEquals_SameObject() {
        Role sameRole = role;

        assertThat(sameRole).isEqualTo(role);
    }

    @Test
    @DisplayName("Test null")
    public void testEquals_Null() {
        assertThat((Role) null).isNotEqualTo(role);
    }

    @Test
    @DisplayName("Test different class")
    public void testEquals_DifferentClass() {
        assertThat(role).isNotEqualTo("This is a different object");
    }

    @Test
    @DisplayName("Test different fields")
    public void testEquals_DifferentFields() {
        Role differentFields = Role.builder()
                .authority(RandomStringUtils.randomAlphanumeric(10).toUpperCase())
                .displayName(RandomStringUtils.randomAlphanumeric(20))
                .description(RandomStringUtils.randomAlphanumeric(20))
                .modifiedAt(NOW.plusMonths(10))
                .modifiedBy(RandomStringUtils.randomAlphanumeric(20))
                .build();

        assertThat(differentFields).isNotEqualTo(role);
    }

    @Test
    @DisplayName("Test same fields")
    public void testEquals_SameFields() {
        Role sameFields = Role.builder()
                .authority(authority)
                .displayName(displayName)
                .description(description)
                .modifiedAt(modifiedAt)
                .modifiedBy(modifiedBy)
                .enabled(enabled)
                .users(List.of(user))
                .build();

        assertThat(sameFields).isEqualTo(role);
    }

    @Test
    @DisplayName("Test same fields hashCode")
    public void testHashCode_SameFields() {
        Role sameFields = Role.builder()
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
}