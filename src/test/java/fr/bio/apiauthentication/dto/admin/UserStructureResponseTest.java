package fr.bio.apiauthentication.dto.admin;

import com.fasterxml.jackson.databind.ObjectMapper;
import fr.bio.apiauthentication.entities.Role;
import fr.bio.apiauthentication.entities.User;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@DisplayName("Test role structure response DTO")
public class UserStructureResponseTest {
    private static final ObjectMapper MAPPER = new ObjectMapper();

    private static final long NOW = LocalDate.now().toEpochDay();

    private UserStructureResponse response;

    private long idUser;
    private String email;
    private String firstName;
    private String lastName;
    private long createdAt;
    private String createdBy;
    private long modifiedAt;
    private String modifiedBy;
    private boolean enabled;
    private Collection<String> roles;

    @BeforeEach
    void setUp() {
        idUser = Long.parseLong(RandomStringUtils.randomNumeric(15));
        email = RandomStringUtils.randomAlphanumeric(5).toUpperCase();
        firstName = RandomStringUtils.randomAlphanumeric(20);
        lastName = RandomStringUtils.randomAlphanumeric(20);
        createdAt = NOW;
        createdBy = RandomStringUtils.randomAlphanumeric(20);
        modifiedAt = NOW;
        modifiedBy = RandomStringUtils.randomAlphanumeric(20);
        enabled = Boolean.parseBoolean(RandomStringUtils.randomNumeric(0, 1));
        roles = List.of(RandomStringUtils.randomAlphanumeric(5).toUpperCase());

        response = new UserStructureResponse(idUser, email, firstName, lastName, createdAt, createdBy, modifiedAt, modifiedBy, enabled, roles);
    }

    @AfterEach
    void tearDown() {
        response = null;
    }

    @Test
    public void testRecordFields() {
        assertThat(response.getIdUser()).isEqualTo(idUser);
        assertThat(response.getEmail()).isEqualTo(email);
        assertThat(response.getFirstName()).isEqualTo(firstName);
        assertThat(response.getLastName()).isEqualTo(lastName);
        assertThat(response.getCreatedAt()).isEqualTo(createdAt);
        assertThat(response.getCreatedBy()).isEqualTo(createdBy);
        assertThat(response.getModifiedAt()).isEqualTo(modifiedAt);
        assertThat(response.getModifiedBy()).isEqualTo(modifiedBy);
        assertThat(response.isEnabled()).isEqualTo(enabled);
        assertThat(response.getRoles()).isEqualTo(roles);
    }

    @Test
    public void testEqualsAndHashCode() {
        final UserStructureResponse requestEquals = new UserStructureResponse(idUser, email, firstName, lastName, createdAt, createdBy, modifiedAt, modifiedBy, enabled, roles);

        assertThat(response).isEqualTo(requestEquals);
        assertThat(response.hashCode()).isEqualTo(requestEquals.hashCode());
    }

    @Test
    public void testNotEqualsAndNotHashCode() {
        final UserStructureResponse requestNotEquals = new UserStructureResponse(
                Long.parseLong(RandomStringUtils.randomNumeric(15)),
                RandomStringUtils.randomAlphanumeric(5).toUpperCase(),
                RandomStringUtils.randomAlphanumeric(20),
                RandomStringUtils.randomAlphanumeric(20),
                LocalDate.now().toEpochDay(),
                RandomStringUtils.randomAlphanumeric(20),
                LocalDate.now().toEpochDay(),
                RandomStringUtils.randomAlphanumeric(20),
                Boolean.parseBoolean(RandomStringUtils.randomNumeric(0, 1)),
                List.of(RandomStringUtils.randomAlphanumeric(5).toUpperCase())
        );

        assertThat(response).isNotEqualTo(requestNotEquals);
        assertThat(response.hashCode()).isNotEqualTo(requestNotEquals.hashCode());
    }

    @Test
    public void testSerialize() throws Exception {
        final String json = MAPPER.writeValueAsString(response);
        final UserStructureResponse actualRequest = MAPPER.readValue(json, UserStructureResponse.class);

        final String expectedJson = "{" +
                "\"id_user\":" + response.getIdUser() + "," +
                "\"email\":\"" + response.getEmail() + "\"," +
                "\"first_name\":\"" + response.getFirstName() + "\"," +
                "\"last_name\":\"" + response.getLastName() + "\"," +
                "\"created_at\":\"" + response.getCreatedAt() + "\"," +
                "\"created_by\":\"" + response.getCreatedBy() + "\"," +
                "\"modified_at\":\"" + response.getModifiedAt() + "\"," +
                "\"modified_by\":\"" + response.getModifiedBy() + "\"," +
                "\"enabled\":" + response.isEnabled() + "," +
                "\"roles\":[" + String.join(",", response.getRoles().stream().map(role -> "\"" + role + "\"").toList()) + "]" +
                "}";
        final UserStructureResponse expectedRequest = MAPPER.readValue(expectedJson, UserStructureResponse.class);

        assertThat(expectedRequest).isEqualTo(actualRequest);
    }

    @Test
    public void testDeserialize() throws Exception {
        final String json = "{" +
                "\"id_user\":" + response.getIdUser() + "," +
                "\"email\":\"" + response.getEmail() + "\"," +
                "\"first_name\":\"" + response.getFirstName() + "\"," +
                "\"last_name\":\"" + response.getLastName() + "\"," +
                "\"created_at\":\"" + response.getCreatedAt() + "\"," +
                "\"created_by\":\"" + response.getCreatedBy() + "\"," +
                "\"modified_at\":\"" + response.getModifiedAt() + "\"," +
                "\"modified_by\":\"" + response.getModifiedBy() + "\"," +
                "\"enabled\":" + response.isEnabled() + "," +
                "\"roles\":[" + String.join(",", response.getRoles().stream().map(role -> "\"" + role + "\"").toList()) + "]" +
                "}";

        final UserStructureResponse requestMapped = MAPPER.readValue(json, UserStructureResponse.class);

        assertThat(requestMapped).usingRecursiveComparison().isEqualTo(response);
    }

    @Test
    public void testFromUser_Success() {
        final User user = generateUser();
        final UserStructureResponse response = UserStructureResponse.fromUser(user);

        assertThat(response).isNotNull();
        assertThat(response.getIdUser()).isEqualTo(user.getIdUser());
        assertThat(response.getEmail()).isEqualTo(user.getEmail());
        assertThat(response.getFirstName()).isEqualTo(user.getFirstName());
        assertThat(response.getLastName()).isEqualTo(user.getLastName());
        assertThat(response.getCreatedAt()).isEqualTo(user.getCreatedAt().toEpochDay());
        assertThat(response.getCreatedBy()).isEqualTo(user.getCreatedBy());
        assertThat(response.getModifiedAt()).isEqualTo(user.getModifiedAt().toEpochDay());
        assertThat(response.getModifiedBy()).isEqualTo(user.getModifiedBy());
        assertThat(response.isEnabled()).isEqualTo(user.isEnabled());

        final List<String> expectedRoles = user.getRoles().stream()
                .map(role -> role.getAuthority() + " : " + role.getDisplayName())
                .toList();
        assertThat(response.getRoles()).isEqualTo(expectedRoles);
    }

    @Test
    public void testFromUser_UserIsNull() {
        final User user = null;
        final UserStructureResponse response = UserStructureResponse.fromUser(user);

        assertThat(response).isNull();
    }

    @Test
    public void testFromUser_UserRolesAreNull() {
        final User user = generateUser();
        user.setRoles(null);
        final UserStructureResponse response = UserStructureResponse.fromUser(user);

        assertThat(response).isNotNull();
        assertThat(response.getIdUser()).isEqualTo(user.getIdUser());
        assertThat(response.getEmail()).isEqualTo(user.getEmail());
        assertThat(response.getFirstName()).isEqualTo(user.getFirstName());
        assertThat(response.getLastName()).isEqualTo(user.getLastName());
        assertThat(response.getCreatedAt()).isEqualTo(user.getCreatedAt().toEpochDay());
        assertThat(response.getCreatedBy()).isEqualTo(user.getCreatedBy());
        assertThat(response.getModifiedAt()).isEqualTo(user.getModifiedAt().toEpochDay());
        assertThat(response.getModifiedBy()).isEqualTo(user.getModifiedBy());
        assertThat(response.isEnabled()).isEqualTo(user.isEnabled());

        final List<String> expectedRoles = List.of();
        assertThat(response.getRoles()).isEqualTo(expectedRoles);
    }

    @Test
    public void testFromUsers_Success() {
        final List<User> users = IntStream.range(0, 5)
                .mapToObj(user -> generateUser())
                .toList();

        final List<UserStructureResponse> responses = UserStructureResponse.fromUsers(users);

        assertThat(responses).isNotNull();
        assertThat(responses.size()).isEqualTo(users.size());

        for (int i = 0; i < users.size(); i++) {
            final User user = users.get(i);
            final UserStructureResponse response = responses.get(i);

            assertThat(response.getIdUser()).isEqualTo(user.getIdUser());
            assertThat(response.getEmail()).isEqualTo(user.getEmail());
            assertThat(response.getFirstName()).isEqualTo(user.getFirstName());
            assertThat(response.getLastName()).isEqualTo(user.getLastName());
            assertThat(response.getCreatedAt()).isEqualTo(user.getCreatedAt().toEpochDay());
            assertThat(response.getCreatedBy()).isEqualTo(user.getCreatedBy());
            assertThat(response.getModifiedAt()).isEqualTo(user.getModifiedAt().toEpochDay());
            assertThat(response.getModifiedBy()).isEqualTo(user.getModifiedBy());
            assertThat(response.isEnabled()).isEqualTo(user.isEnabled());

            final List<String> expectedRoles = user.getRoles().stream()
                    .map(role -> role.getAuthority() + " : " + role.getDisplayName())
                    .toList();
            assertThat(response.getRoles()).isEqualTo(expectedRoles);
        }
    }

    @Test
    public void testFromUsers_UsersAreNull() {
        final List<User> users = null;
        final List<UserStructureResponse> responses = UserStructureResponse.fromUsers(users);

        assertThat(responses).isEqualTo(List.of());
    }

    @Test
    public void testToString() {
        final User user = generateUser();
        final UserStructureResponse response = UserStructureResponse.fromUser(user);

        final String exceptedRolesToString = user.getRoles().stream()
                .map(role -> role.getAuthority() + " : " + role.getDisplayName())
                .collect(Collectors.joining(", "));
        final String exceptedToString = "UserStructureResponse(idUser=" + response.getIdUser() +
                ", email=" + user.getEmail() +
                ", firstName=" + user.getFirstName() +
                ", lastName=" + user.getLastName() +
                ", createdAt=" + user.getCreatedAt().toEpochDay() +
                ", createdBy=" + user.getCreatedBy() +
                ", modifiedAt=" + user.getModifiedAt().toEpochDay() +
                ", modifiedBy=" + user.getModifiedBy() +
                ", enabled=" + user.isEnabled() +
                ", roles=[" + exceptedRolesToString + "])";
        final String exceptedToStringLombok = "UserStructureResponse.UserStructureResponseBuilder(idUser=" + response.getIdUser() +
                ", email=" + user.getEmail() +
                ", firstName=" + user.getFirstName() +
                ", lastName=" + user.getLastName() +
                ", createdAt=" + user.getCreatedAt().toEpochDay() +
                ", createdBy=" + user.getCreatedBy() +
                ", modifiedAt=" + user.getModifiedAt().toEpochDay() +
                ", modifiedBy=" + user.getModifiedBy() +
                ", enabled=" + user.isEnabled() +
                ", roles=[" + exceptedRolesToString + "])";

        assertThat(response).isNotNull();
        assertThat(response.toString()).isEqualTo(exceptedToString);
        assertThat(UserStructureResponse.builder()
                .idUser(user.getIdUser())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .createdAt(user.getCreatedAt().toEpochDay())
                .createdBy(user.getCreatedBy())
                .modifiedAt(user.getModifiedAt().toEpochDay())
                .modifiedBy(user.getModifiedBy())
                .enabled(user.isEnabled())
                .roles(user.getRoles().stream().map(role -> role.getAuthority() + " : " + role.getDisplayName()).collect(Collectors.toList()))
                .toString()
        ).isEqualTo(exceptedToStringLombok);
    }

    private User generateUser() {
        return User.builder()
                .idUser((long) (Math.random() * 1000))
                .email(RandomStringUtils.randomAlphanumeric(10) + "@test.com")
                .password(RandomStringUtils.randomAlphanumeric(30))
                .firstName(RandomStringUtils.randomAlphanumeric(5))
                .lastName(RandomStringUtils.randomAlphanumeric(7))
                .createdAt(LocalDate.now())
                .createdBy(RandomStringUtils.randomAlphanumeric(10))
                .modifiedAt(LocalDate.now())
                .modifiedBy(RandomStringUtils.randomAlphanumeric(10))
                .enabled(Math.random() < 0.5)
                .roles(generateRoles())
                .build();
    }

    private List<Role> generateRoles() {
        return IntStream.range(0, 5)
                .mapToObj(role -> Role.builder()
                        .idRole((long) (Math.random() * 1000))
                        .authority(RandomStringUtils.randomAlphanumeric(10))
                        .displayName(RandomStringUtils.randomAlphanumeric(10))
                        .description(RandomStringUtils.randomAlphanumeric(20))
                        .build())
                .toList();
    }
}