package fr.bio.apiauthentication.dto.admin;

import com.fasterxml.jackson.databind.ObjectMapper;
import fr.bio.apiauthentication.entities.LoginHistory;
import fr.bio.apiauthentication.entities.User;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.stream.IntStream;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@DisplayName("Test login history structure response DTO")
public class LoginHistoryStructureResponseTest {
    private static final ObjectMapper MAPPER = new ObjectMapper();

    private static final long NOW = LocalDateTime.now().toInstant(ZoneOffset.UTC).toEpochMilli();

    private LoginHistoryStructureResponse response;

    private long idLoginHistory;
    private long idUser;
    private String userEmail;
    private long dateLogin;

    @BeforeEach
    void setUp() {
        idLoginHistory = Long.parseLong(RandomStringUtils.randomNumeric(10));
        idUser = Long.parseLong(RandomStringUtils.randomNumeric(10));
        userEmail = RandomStringUtils.randomAlphabetic(10) + "@test.com";
        dateLogin = NOW;

        response = new LoginHistoryStructureResponse(idLoginHistory, idUser, userEmail, dateLogin);
    }

    @AfterEach
    void tearDown() {
        response = null;
    }

    @Test
    public void testRecordFields() {
        assertThat(response).isNotNull();
        assertThat(response.getIdLoginHistory()).isEqualTo(idLoginHistory);
        assertThat(response.getIdUser()).isEqualTo(idUser);
        assertThat(response.getUserEmail()).isEqualTo(userEmail);
        assertThat(response.getDateLogin()).isEqualTo(dateLogin);
    }

    @Test
    public void testEqualsAndHashCode() {
        LoginHistoryStructureResponse requestEquals = new LoginHistoryStructureResponse(idLoginHistory, idUser, userEmail, dateLogin);

        assertThat(response).isEqualTo(requestEquals);
        assertThat(response.hashCode()).isEqualTo(requestEquals.hashCode());
    }

    @Test
    public void testNotEqualsAndNotHashCode() {
        LoginHistoryStructureResponse requestNotEquals = new LoginHistoryStructureResponse(
                idLoginHistory = Long.parseLong(RandomStringUtils.randomNumeric(10)),
                idUser = Long.parseLong(RandomStringUtils.randomNumeric(10)),
                userEmail = RandomStringUtils.randomAlphabetic(10) + "@test.com",
                dateLogin = NOW
        );

        assertThat(response).isNotEqualTo(requestNotEquals);
        assertThat(response.hashCode()).isNotEqualTo(requestNotEquals.hashCode());
    }

    @Test
    public void testSerialize() throws Exception {
        String json = MAPPER.writeValueAsString(response);
        LoginHistoryStructureResponse actualRequest = MAPPER.readValue(json, LoginHistoryStructureResponse.class);

        String expectedJson = "{" +
                "\"id_login_history\":" + response.getIdLoginHistory() + "," +
                "\"id_user\":\"" + response.getIdUser() + "\"," +
                "\"user_email\":\"" + response.getUserEmail() + "\"," +
                "\"date_login\":\"" + response.getDateLogin() + "\"" +
                "}";
        LoginHistoryStructureResponse expectedRequest = MAPPER.readValue(expectedJson, LoginHistoryStructureResponse.class);

        assertThat(expectedRequest).isEqualTo(actualRequest);
    }

    @Test
    public void testDeserialize() throws Exception {
        String json = "{" +
                "\"id_login_history\":" + response.getIdLoginHistory() + "," +
                "\"id_user\":\"" + response.getIdUser() + "\"," +
                "\"user_email\":\"" + response.getUserEmail() + "\"," +
                "\"date_login\":\"" + response.getDateLogin() + "\"" +
                "}";

        LoginHistoryStructureResponse requestMapped = MAPPER.readValue(json, LoginHistoryStructureResponse.class);

        assertThat(requestMapped).usingRecursiveComparison().isEqualTo(response);
    }

    @Test
    public void testFromLoginHistory_Success() {
        LoginHistory loginHistory = generateLoginHistory();
        LoginHistoryStructureResponse response = LoginHistoryStructureResponse.fromLoginHistory(loginHistory);

        assertThat(response).isNotNull();
        assertThat(response.getIdLoginHistory()).isEqualTo(loginHistory.getIdLoginHistory());
        assertThat(response.getIdUser()).isEqualTo(response.getIdUser());
        assertThat(response.getUserEmail()).isEqualTo(loginHistory.getUser().getEmail());
        assertThat(response.getDateLogin()).isEqualTo(loginHistory.getDateLogin().toInstant(ZoneOffset.UTC).toEpochMilli());
    }

    @Test
    public void testFromLoginHistory_LoginHistoryIsNull() {
        LoginHistoryStructureResponse response = LoginHistoryStructureResponse.fromLoginHistory(null);

        assertThat(response).isNull();
    }

    @Test
    public void testFromLoginHistories_Success() {
        List<LoginHistory> loginHistories = IntStream.range(0, 5)
                .mapToObj(loginHistory -> generateLoginHistory())
                .toList();
        List<LoginHistoryStructureResponse> responses = LoginHistoryStructureResponse.fromLoginHistories(loginHistories);

        assertThat(responses).isNotNull();
        assertThat(responses.size()).isEqualTo(loginHistories.size());

        for (int i = 0; i < loginHistories.size(); i++) {
            LoginHistory loginHistory = loginHistories.get(i);
            LoginHistoryStructureResponse response = responses.get(i);

            assertThat(response.getIdLoginHistory()).isEqualTo(loginHistory.getIdLoginHistory());
            assertThat(response.getIdUser()).isEqualTo(loginHistory.getUser().getIdUser());
            assertThat(response.getUserEmail()).isEqualTo(loginHistory.getUser().getEmail());
            assertThat(response.getDateLogin()).isEqualTo(loginHistory.getDateLogin().toInstant(ZoneOffset.UTC).toEpochMilli());
        }
    }

    @Test
    public void testFromLoginHistories_LoginHistorisIsNull() {
        List<LoginHistoryStructureResponse> responses = LoginHistoryStructureResponse.fromLoginHistories(null);

        assertThat(responses).isEqualTo(List.of());
    }

    @Test
    public void testToString() {
        final LoginHistory loginHistory = generateLoginHistory();
        final LoginHistoryStructureResponse response = LoginHistoryStructureResponse.fromLoginHistory(loginHistory);

        final String exceptedToString = "LoginHistoryStructureResponse(idLoginHistory=%s, idUser=%s, userEmail=%s, dateLogin=%s)".formatted(loginHistory.getIdLoginHistory(), loginHistory.getUser().getIdUser(), loginHistory.getUser().getEmail(), loginHistory.getDateLogin().toInstant(ZoneOffset.UTC).toEpochMilli());
        final String exceptedToStringLombok = "LoginHistoryStructureResponse.LoginHistoryStructureResponseBuilder(idLoginHistory=%s, idUser=%s, userEmail=%s, dateLogin=%s)".formatted(loginHistory.getIdLoginHistory(), loginHistory.getUser().getIdUser(), loginHistory.getUser().getEmail(), loginHistory.getDateLogin().toInstant(ZoneOffset.UTC).toEpochMilli());

        assertThat(response).isNotNull();
        assertThat(response.toString()).isEqualTo(exceptedToString);
        assertThat(LoginHistoryStructureResponse.builder()
                .idLoginHistory(loginHistory.getIdLoginHistory())
                .idUser(loginHistory.getUser().getIdUser())
                .userEmail(loginHistory.getUser().getEmail())
                .dateLogin(loginHistory.getDateLogin().toInstant(ZoneOffset.UTC).toEpochMilli())
                .toString()
        ).isEqualTo(exceptedToStringLombok);
    }

    private LoginHistory generateLoginHistory() {
        return LoginHistory.builder()
                .idLoginHistory(Long.parseLong(RandomStringUtils.randomNumeric(10)))
                .user(generateUser())
                .dateLogin(LocalDateTime.now().plusYears(10))
                .build();
    }

    private User generateUser() {
        return User.builder()
                .email(RandomStringUtils.randomAlphanumeric(10) + "@test.com")
                .password(RandomStringUtils.randomAlphanumeric(30))
                .firstName(RandomStringUtils.randomAlphanumeric(5))
                .lastName(RandomStringUtils.randomAlphanumeric(7))
                .build();
    }
}