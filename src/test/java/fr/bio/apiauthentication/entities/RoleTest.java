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

@DisplayName("Test Role JPA Entity")
@DataJpaTest
public class RoleTest {
    @Autowired
    private TestEntityManager entityManager;

    private Role role;

    @BeforeEach
    void setUp() {
        role = Role.builder()
                .roleName("USER")
                .build();
    }

    @AfterEach
    void tearDown() {
        role = null;
    }

    @Test
    @Transactional
    public void testCreateRole() {
        Role persistedRole = entityManager.persist(role);

        assertThat(persistedRole).isNotNull();
    }

    @Test
    @Transactional
    public void testUpdateRole() {
        Role newRole = role;

        newRole.setRoleName("ADMIN");
        newRole.setEnabled(false);

        Role persistedRole = entityManager.persist(newRole);

        assertThat(persistedRole).isNotNull();
    }

    @Test
    @Transactional
    public void testEquals_SameObject() {
        assertThat(role).isEqualTo(role);
    }

    @Test
    @Transactional
    public void testEquals_Null() {
        assertThat(role).isNotEqualTo(null);
    }

    @Test
    @Transactional
    public void testEquals_DifferentClass() {
        assertThat(role).isNotEqualTo("This is a different object");
    }

    @Test
    @Transactional
    public void testEquals_DifferentFields() {
        Role differentFields = Role.builder()
                .roleName("ADMIN")
                .enabled(false)
                .build();

        assertThat(role).isNotEqualTo(differentFields);
    }

    @Test
    @Transactional
    public void testEquals_SameFields() {
        Role sameFields = Role.builder()
                .roleName("USER")
                .build();

        assertThat(role).isEqualTo(sameFields);
    }

    @Test
    @Transactional
    public void testHashCode_SameFields() {
        Role sameFields = Role.builder()
                .roleName("USER")
                .build();

        assertThat(role.hashCode()).isEqualTo(sameFields.hashCode());
    }
}
