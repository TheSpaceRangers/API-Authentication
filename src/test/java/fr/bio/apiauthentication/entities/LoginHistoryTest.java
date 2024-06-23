package fr.bio.apiauthentication.entities;

import jakarta.transaction.Transactional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@DisplayName("Test LoginHistory JPA Entity")
@DataJpaTest
@Transactional
public class LoginHistoryTest {
    @Autowired
    private TestEntityManager entityManager;

    private User user;
    private LoginHistory loginHistory;

    private final LocalDateTime NOW = LocalDateTime.now();

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
        entityManager.persist(user);

        loginHistory = LoginHistory.builder()
                .user(user)
                .ipAddress("This is IP Address")
                .dateLogin(NOW)
                .build();
    }

    @AfterEach
    void tearDown() {
        entityManager.flush();

        user = null;
        loginHistory = null;
    }

    @Test
    @DisplayName("Test create login history")
    public void testCreateLoginHistory() {
        loginHistory = entityManager.persistAndFlush(loginHistory);
    }

    @Test
    @DisplayName("Test update login history")
    public void testUpdateLoginHistory() {
        loginHistory.setUser(user);
        loginHistory.setIpAddress("This is new ip address");
        loginHistory.setDateLogin(LocalDateTime.of(2020, 1, 1, 1, 1));

        loginHistory = entityManager.persistAndFlush(loginHistory);
    }

    @Test
    @DisplayName("Test same object")
    public void testEquals_SameObject() {
        assertThat(loginHistory).isEqualTo(loginHistory);
    }

    @Test
    @DisplayName("Test null")
    public void testEquals_Null() {
        assertThat(loginHistory).isNotEqualTo(null);
    }

    @Test
    @DisplayName("Test different class")
    public void testEquals_DifferentClass() {
        assertThat(loginHistory).isNotEqualTo("This is a different object");
    }

    @Test
    @DisplayName("Test different fields")
    public void testEquals_DifferentFields() {
        LoginHistory differentFields = LoginHistory.builder()
                .user(user)
                .ipAddress("This is different IP Address")
                .dateLogin(LocalDateTime.now())
                .build();

        assertThat(loginHistory).isNotEqualTo(differentFields);
    }

    @Test
    @DisplayName("Test same fields")
    public void testEquals_SameFields() {
        LoginHistory sameFields = LoginHistory.builder()
                .user(user)
                .ipAddress("This is IP Address")
                .dateLogin(NOW)
                .build();

        assertThat(loginHistory).isEqualTo(sameFields);
    }

    @Test
    @DisplayName("Test same fields hashCode")
    public void testHashCode_SameFields() {
        LoginHistory sameFields = LoginHistory.builder()
                .user(user)
                .ipAddress("This is IP Address")
                .dateLogin(NOW)
                .build();

        assertThat(loginHistory.hashCode()).isEqualTo(sameFields.hashCode());
    }
}
