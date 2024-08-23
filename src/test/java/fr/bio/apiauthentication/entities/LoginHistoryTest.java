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
import java.time.LocalDateTime;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@DisplayName("Test LoginHistory JPA Entity")
@DataJpaTest
@Transactional
public class LoginHistoryTest {
    @Autowired
    private TestEntityManager entityManager;

    private LoginHistory loginHistory;

    private final LocalDateTime NOW = LocalDateTime.now();

    private String ipAddress;
    private LocalDateTime dateLogin;

    private User user;

    @BeforeEach
    void setUp() {
        ipAddress = RandomStringUtils.randomAlphanumeric(10);
        dateLogin = NOW;

        user = User.builder()
                .email(RandomStringUtils.randomAlphanumeric(10) + "@test.com")
                .password(RandomStringUtils.randomAlphanumeric(30))
                .firstName(RandomStringUtils.randomAlphanumeric(20))
                .lastName(RandomStringUtils.randomAlphanumeric(20))
                .createdAt(LocalDate.now())
                .createdBy(RandomStringUtils.randomAlphanumeric(20))
                .modifiedAt(LocalDate.now())
                .modifiedBy(RandomStringUtils.randomAlphanumeric(20))
                .enabled(Boolean.parseBoolean(RandomStringUtils.randomNumeric(0, 1)))
                .build();
        entityManager.persist(user);

        loginHistory = LoginHistory.builder()
                .user(user)
                .ipAddress(ipAddress)
                .dateLogin(dateLogin)
                .build();
    }

    @AfterEach
    void tearDown() {
        user = null;
        loginHistory = null;
    }

    @Test
    @DisplayName("Test create login history")
    public void testCreateLoginHistory() {
        final LoginHistory savedLoginHistory = entityManager.persistAndFlush(loginHistory);

        assertThat(savedLoginHistory).isNotNull();
        assertThat(savedLoginHistory).isEqualTo(loginHistory);
        assertThat(savedLoginHistory).usingRecursiveComparison().isEqualTo(loginHistory);
    }

    @Test
    @DisplayName("Test update login history")
    public void testUpdateLoginHistory() {
        final LoginHistory savedLoginHistory = entityManager.persistAndFlush(loginHistory);


        final long idLoginHistory = Long.parseLong(RandomStringUtils.randomNumeric(10));
        ipAddress = RandomStringUtils.randomAlphanumeric(10);
        dateLogin = NOW.plusDays(20);

        savedLoginHistory.setIdLoginHistory(idLoginHistory);
        savedLoginHistory.setUser(user);
        savedLoginHistory.setIpAddress(ipAddress);
        savedLoginHistory.setDateLogin(NOW);

        final LoginHistory updatedLoginHistory = entityManager.persist(savedLoginHistory);

        assertThat(updatedLoginHistory).isNotNull();
        assertThat(updatedLoginHistory).isEqualTo(loginHistory);
        assertThat(updatedLoginHistory).usingRecursiveComparison().isEqualTo(loginHistory);
    }

    @Test
    @DisplayName("Test same object")
    public void testEqualsAndHashCode_SameObject() {
        final LoginHistory sameLoginHistory = loginHistory;

        assertThat(sameLoginHistory).isEqualTo(loginHistory);
        assertThat(sameLoginHistory.hashCode()).isEqualTo(loginHistory.hashCode());
    }

    @Test
    @DisplayName("Test null")
    public void testEqualsAndHashCode_Null() {
        assertThat((LoginHistory) null).isNotEqualTo(loginHistory);
        assertThat((LoginHistory) null).isNotEqualTo(loginHistory.hashCode());
    }

    @Test
    @DisplayName("Test different class")
    public void testEqualsAndHashCode_DifferentClass() {
        assertThat(loginHistory).isNotEqualTo("This is a different object");
        assertThat(loginHistory.hashCode()).isNotEqualTo("This is a different object".hashCode());
    }

    @Test
    @DisplayName("Test different fields")
    public void testEqualsAndHashCode_DifferentFields() {
        LoginHistory differentFields = LoginHistory.builder()
                .user(user)
                .ipAddress(RandomStringUtils.randomAlphanumeric(20))
                .dateLogin(LocalDateTime.now().plusYears(20))
                .build();

        assertThat(differentFields).isNotEqualTo(loginHistory);
        assertThat(differentFields.hashCode()).isNotEqualTo(loginHistory.hashCode());
    }

    @Test
    @DisplayName("Test same fields")
    public void testEqualsAndHashCode_SameFields() {
        LoginHistory sameFields = LoginHistory.builder()
                .user(user)
                .ipAddress(ipAddress)
                .dateLogin(dateLogin)
                .build();

        assertThat(loginHistory).isEqualTo(sameFields);
        assertThat(loginHistory.hashCode()).isEqualTo(sameFields.hashCode());
    }

    @Test
    public void testToString() {
        final LoginHistory savedLoginHistory = entityManager.persistAndFlush(loginHistory);
        final String exceptedString = "LoginHistory(" +
                "idLoginHistory=" + savedLoginHistory.getIdLoginHistory() + ", " +
                "user=" + savedLoginHistory.getUser() + ", " +
                "ipAddress=" + savedLoginHistory.getIpAddress() + ", " +
                "dateLogin=" + savedLoginHistory.getDateLogin() +
                ")";
        final String exceptedStringLombok = "LoginHistory.LoginHistoryBuilder(" +
                "idLoginHistory=" + savedLoginHistory.getIdLoginHistory() + ", " +
                "user=" + savedLoginHistory.getUser() + ", " +
                "ipAddress=" + savedLoginHistory.getIpAddress() + ", " +
                "dateLogin=" + savedLoginHistory.getDateLogin() +
                ")";

        assertThat(savedLoginHistory).isNotNull();
        assertThat(exceptedString).isEqualTo(savedLoginHistory.toString());
        assertThat(LoginHistory.builder()
                .idLoginHistory(savedLoginHistory.getIdLoginHistory())
                .ipAddress(savedLoginHistory.getIpAddress())
                .dateLogin(savedLoginHistory.getDateLogin())
                .user(savedLoginHistory.getUser())
                .toString()
        ).isEqualTo(exceptedStringLombok);
    }
}