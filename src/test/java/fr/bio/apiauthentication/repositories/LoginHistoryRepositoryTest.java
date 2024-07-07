package fr.bio.apiauthentication.repositories;

import fr.bio.apiauthentication.entities.LoginHistory;
import fr.bio.apiauthentication.entities.User;
import jakarta.transaction.Transactional;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@DisplayName("Test LoginHistory JPA Repository")
@DataJpaTest
@Transactional
public class LoginHistoryRepositoryTest {
    private static final LocalDateTime NOW = LocalDateTime.now();

    @Autowired
    private LoginHistoryRepository loginHistoryRepository;

    @Autowired
    private UserRepository userRepository;

    private LoginHistory loginHistory;
    private User user;

    private String ipAddress;
    private LocalDateTime dateLogin;

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
        userRepository.save(user);

        loginHistory = LoginHistory.builder()
                .ipAddress(ipAddress)
                .dateLogin(dateLogin)
                .user(user)
                .build();

        loginHistoryRepository.deleteAll();
    }

    @AfterEach
    void tearDown() {
        loginHistoryRepository.deleteAll();
    }

    @Test
    @DisplayName("Test save login history")
    public void testSaveRole() {
        LoginHistory savedLoginHistory = loginHistoryRepository.save(loginHistory);

        assertThat(savedLoginHistory).isNotNull();
        assertThat(savedLoginHistory.getIpAddress()).isEqualTo(ipAddress);
        assertThat(savedLoginHistory.getDateLogin()).isEqualTo(dateLogin);
        assertThat(savedLoginHistory.getUser()).isEqualTo(user);
    }

    @Test
    @DisplayName("Test find all login history by user")
    public void testFindAllLoginHistoryByUser() {
        LoginHistory savedLoginHistory = loginHistoryRepository.save(loginHistory);

        List<LoginHistory> expectedLoginHistoryList = List.of(savedLoginHistory);
        List<LoginHistory> foundLoginHistoryList = loginHistoryRepository.findAllByUser(user);

        assertThat(foundLoginHistoryList.size()).isEqualTo(1);
        assertThat(foundLoginHistoryList).isEqualTo(expectedLoginHistoryList);
    }
}