package fr.bio.apiauthentication.services;

import fr.bio.apiauthentication.components.HttpHeadersUtil;
import fr.bio.apiauthentication.dto.admin.LoginHistoryRequest;
import fr.bio.apiauthentication.dto.admin.LoginHistoryStructureResponse;
import fr.bio.apiauthentication.entities.LoginHistory;
import fr.bio.apiauthentication.entities.Role;
import fr.bio.apiauthentication.entities.User;
import fr.bio.apiauthentication.exceptions.not_found.RoleNotFoundException;
import fr.bio.apiauthentication.repositories.LoginHistoryRepository;
import fr.bio.apiauthentication.repositories.UserRepository;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@DisplayName("Test login history admin service")
public class AdminLoginHistoryServiceTest {
    private static final LocalDate NOW = LocalDate.now();
    private static final LocalDateTime DATE_TIME = LocalDateTime.now();

    @Mock
    private LoginHistoryRepository loginHistoryRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private JwtService jwtService;

    @Mock
    private HttpHeadersUtil httpHeadersUtil;

    @InjectMocks
    private AdminLoginHistoryService adminLoginHistoryService;

    private HttpHeaders headers;

    private String token;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        token = jwtService.generateToken(generateUser());

        headers = httpHeadersUtil.createHeaders(token);
    }

    @AfterEach
    void tearDown() {
        token = null;
        headers = null;
    }

    @Test
    public void testGetAllLoginHistory() {
        final List<LoginHistory> loginHistories = IntStream.range(0, 10)
                .mapToObj(loginHistory -> generateLoginHistory(null))
                .toList();
        final List<LoginHistoryStructureResponse> exceptedResponses = LoginHistoryStructureResponse.fromLoginHistories(loginHistories);

        when(loginHistoryRepository.findAll()).thenReturn(loginHistories);
        when(httpHeadersUtil.createHeaders(token)).thenReturn(headers);

        final ResponseEntity<List<LoginHistoryStructureResponse>> response = adminLoginHistoryService.getAllLoginHistory(token);

        assertThat(response).isNotNull();
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(exceptedResponses);
        assertThat(response.getBody()).usingRecursiveComparison().isEqualTo(exceptedResponses);
    }

    @Test
    public void testGetLoginHistoryByUser_Success() {
        final User user = generateUser();
        final List<LoginHistory> loginHistories = IntStream.range(0, 10)
                .mapToObj(loginHistory -> generateLoginHistory(user))
                .toList();
        final List<LoginHistoryStructureResponse> exceptedResponses = LoginHistoryStructureResponse.fromLoginHistories(loginHistories);

        final LoginHistoryRequest request = new LoginHistoryRequest(user.getEmail());

        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        when(loginHistoryRepository.findAllByUser(user)).thenReturn(loginHistories);
        when(httpHeadersUtil.createHeaders(token)).thenReturn(headers);

        final ResponseEntity<List<LoginHistoryStructureResponse>> response = adminLoginHistoryService.getAllLoginHistoryByUser(token, request);

        assertThat(response).isNotNull();
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(exceptedResponses);
        assertThat(response.getBody()).usingRecursiveComparison().isEqualTo(exceptedResponses);

        verify(userRepository, times(1)).findByEmail(user.getEmail());
        verify(loginHistoryRepository, times(1)).findAllByUser(user);
    }

    @Test
    public void testGetLoginHistoryByUser_UserNotFound() {
        final User user = generateUser();
        final List<LoginHistory> loginHistories = IntStream.range(0, 10)
                .mapToObj(loginHistory -> generateLoginHistory(user))
                .toList();
        final List<LoginHistoryStructureResponse> exceptedResponses = LoginHistoryStructureResponse.fromLoginHistories(loginHistories);

        final LoginHistoryRequest request = new LoginHistoryRequest(user.getEmail());

        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class, () -> adminLoginHistoryService.getAllLoginHistoryByUser(token, request));

        verify(userRepository, times(1)).findByEmail(user.getEmail());
    }

    private LoginHistory generateLoginHistory(User user) {
        return LoginHistory.builder()
                .user(user == null ? generateUser() : user)
                .ipAddress(RandomStringUtils.randomNumeric(10))
                .dateLogin(DATE_TIME.plusDays(10))
                .build();
    }

    private User generateUser() {
        return User.builder()
                .email(RandomStringUtils.randomAlphanumeric(10) + "@test.com")
                .password(RandomStringUtils.randomAlphanumeric(30))
                .firstName(RandomStringUtils.randomAlphanumeric(20))
                .lastName(RandomStringUtils.randomAlphanumeric(20))
                .createdAt(NOW)
                .createdBy(RandomStringUtils.randomAlphanumeric(20))
                .modifiedAt(NOW)
                .modifiedBy(RandomStringUtils.randomAlphanumeric(20))
                .enabled(true)
                .roles(IntStream.range(0, 2)
                        .mapToObj(role -> generateRole())
                        .toList()
                ).build();
    }

    private Role generateRole() {
        return Role.builder()
                .authority(RandomStringUtils.randomAlphanumeric(10).toUpperCase())
                .displayName(RandomStringUtils.randomAlphanumeric(20))
                .description(RandomStringUtils.randomAlphanumeric(20))
                .modifiedAt(NOW)
                .modifiedBy(RandomStringUtils.randomAlphanumeric(20))
                .enabled(true)
                .users(null)
                .build();
    }
}
