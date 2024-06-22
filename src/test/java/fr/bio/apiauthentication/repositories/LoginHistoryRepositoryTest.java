package fr.bio.apiauthentication.repositories;

import fr.bio.apiauthentication.entities.LoginHistory;
import fr.bio.apiauthentication.entities.Role;
import fr.bio.apiauthentication.entities.User;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@DisplayName("Test LoginHistory JPA Repository")
@DataJpaTest
@Transactional
public class LoginHistoryRepositoryTest {
    @Autowired
    private LoginHistoryRepository loginHistoryRepository;

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        loginHistoryRepository.deleteAll();
    }

    @AfterEach
    void tearDown() {
        loginHistoryRepository.deleteAll();
    }

    private final LocalDateTime NOW = LocalDateTime.now();

    @Test
    @DisplayName("Test save login history")
    public void testSaveRole() {
        LoginHistory loginHistory = LoginHistory.builder()
                .user(null)
                .ipAddress("This is IP Address")
                .dateLogin(NOW)
                .build();
        LoginHistory savedLoginHistory = loginHistoryRepository.save(loginHistory);

        assertThat(savedLoginHistory).isNotNull();
        assertThat(savedLoginHistory.getIpAddress()).isEqualTo(loginHistory.getIpAddress());
    }

    @Test
    @DisplayName("Test find all login history by user")
    public void testFindByRoleName() {
        User user = User.builder()
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
        userRepository.save(user);

        LoginHistory loginHistory = LoginHistory.builder()
                .user(user)
                .ipAddress("This is IP Address")
                .dateLogin(NOW)
                .build();
        LoginHistory savedLoginHistory = loginHistoryRepository.save(loginHistory);

        List<LoginHistory> actualLogionHistory = List.of(savedLoginHistory);
        List<LoginHistory> foundLoginHistoryList = loginHistoryRepository.findAllByUser(user);

        assertThat(foundLoginHistoryList.size()).isEqualTo(1);
        assertThat(foundLoginHistoryList).isEqualTo(actualLogionHistory);
    }
}
