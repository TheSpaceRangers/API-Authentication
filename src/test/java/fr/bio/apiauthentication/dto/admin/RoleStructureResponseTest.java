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
public class RoleStructureResponseTest {
    private static final ObjectMapper MAPPER = new ObjectMapper();

    private static final long NOW = LocalDate.now().toEpochDay();

    private RoleStructureResponse response;

    private long idRole;
    private String authority;
    private String displayName;
    private String description;
    private long modifiedAt;
    private String modifiedBy;
    private boolean enabled;
    private Collection<String> users;

    @BeforeEach
    void setUp() {
        idRole = Long.parseLong(RandomStringUtils.randomNumeric(15));
        authority = RandomStringUtils.randomAlphanumeric(5).toUpperCase();
        displayName = RandomStringUtils.randomAlphanumeric(20);
        description = RandomStringUtils.randomAlphanumeric(20);
        modifiedAt = NOW;
        modifiedBy = RandomStringUtils.randomAlphanumeric(20);
        enabled = Boolean.parseBoolean(RandomStringUtils.randomNumeric(0, 1));
        users = List.of(RandomStringUtils.randomAlphanumeric(5).toUpperCase());

        response = new RoleStructureResponse(idRole, authority, displayName, description, modifiedAt, modifiedBy, enabled, users);
    }

    @AfterEach
    void tearDown() {
        response = null;
    }

    @Test
    public void testRecordFields() {
        assertThat(response.getIdRole()).isEqualTo(idRole);
        assertThat(response.getAuthority()).isEqualTo(authority);
        assertThat(response.getDisplayName()).isEqualTo(displayName);
        assertThat(response.getDescription()).isEqualTo(description);
        assertThat(response.getModifiedAt()).isEqualTo(modifiedAt);
        assertThat(response.getModifiedBy()).isEqualTo(modifiedBy);
        assertThat(response.isEnabled()).isEqualTo(enabled);
        assertThat(response.getUsers()).isEqualTo(users);
    }

    @Test
    public void testEqualsAndHashCode() {
        final RoleStructureResponse requestEquals = new RoleStructureResponse(idRole, authority, displayName, description, modifiedAt, modifiedBy, enabled, users);

        assertThat(response).isEqualTo(requestEquals);
        assertThat(response.hashCode()).isEqualTo(requestEquals.hashCode());
    }

    @Test
    public void testNotEqualsAndNotHashCode() {
        final RoleStructureResponse requestNotEquals = new RoleStructureResponse(
                Long.parseLong(RandomStringUtils.randomNumeric(15)),
                RandomStringUtils.randomAlphanumeric(5).toUpperCase(),
                RandomStringUtils.randomAlphanumeric(20),
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
        final RoleStructureResponse actualRequest = MAPPER.readValue(json, RoleStructureResponse.class);

        final String expectedJson = "{" +
                "\"id_role\":" + response.getIdRole() + "," +
                "\"authority\":\"" + response.getAuthority() + "\"," +
                "\"display_name\":\"" + response.getDisplayName() + "\"," +
                "\"description\":\"" + response.getDescription() + "\"," +
                "\"modified_at\":\"" + response.getModifiedAt() + "\"," +
                "\"modified_by\":\"" + response.getModifiedBy() + "\"," +
                "\"enabled\":" + response.isEnabled() + "," +
                "\"users\":[" + String.join(",", response.getUsers().stream().map(user -> "\"" + user + "\"").toList()) + "]" +
                "}";
        final RoleStructureResponse expectedRequest = MAPPER.readValue(expectedJson, RoleStructureResponse.class);

        assertThat(expectedRequest).isEqualTo(actualRequest);
    }

    @Test
    public void testDeserialize() throws Exception {
        final String json = "{" +
                "\"id_role\":" + response.getIdRole() + "," +
                "\"authority\":\"" + response.getAuthority() + "\"," +
                "\"display_name\":\"" + response.getDisplayName() + "\"," +
                "\"description\":\"" + response.getDescription() + "\"," +
                "\"modified_at\":\"" + response.getModifiedAt() + "\"," +
                "\"modified_by\":\"" + response.getModifiedBy() + "\"," +
                "\"enabled\":" + response.isEnabled() + "," +
                "\"users\":[" + String.join(",", response.getUsers().stream().map(user -> "\"" + user + "\"").toList()) + "]" +
                "}";

        final RoleStructureResponse requestMapped = MAPPER.readValue(json, RoleStructureResponse.class);

        assertThat(requestMapped).usingRecursiveComparison().isEqualTo(response);
    }

    @Test
    public void testFromRole_Success() {
        final Role role = generateRole();
        final RoleStructureResponse response = RoleStructureResponse.fromRole(role);

        assertThat(response).isNotNull();
        assertThat(response.getIdRole()).isEqualTo(role.getIdRole());
        assertThat(response.getAuthority()).isEqualTo(role.getAuthority());
        assertThat(response.getDisplayName()).isEqualTo(role.getDisplayName());
        assertThat(response.getDescription()).isEqualTo(role.getDescription());
        assertThat(response.getModifiedAt()).isEqualTo(role.getModifiedAt().toEpochDay());
        assertThat(response.getModifiedBy()).isEqualTo(role.getModifiedBy());
        assertThat(response.isEnabled()).isEqualTo(role.isEnabled());

        final List<String> expectedUsers = role.getUsers().stream()
                .map(user -> user.getFirstName() + " " + user.getLastName())
                .collect(Collectors.toList());
        assertThat(response.getUsers()).isEqualTo(expectedUsers);
    }

    @Test
    public void testFromRole_RoleIsNull() {
        final RoleStructureResponse response = RoleStructureResponse.fromRole(null);

        assertThat(response).isNull();
    }

    @Test
    public void testFromRole_UsersAreNull() {
        final Role role = generateRole();
        role.setUsers(null);
        final RoleStructureResponse response = RoleStructureResponse.fromRole(role);

        assertThat(response).isNotNull();
        assertThat(response.getIdRole()).isEqualTo(role.getIdRole());
        assertThat(response.getAuthority()).isEqualTo(role.getAuthority());
        assertThat(response.getDisplayName()).isEqualTo(role.getDisplayName());
        assertThat(response.getDescription()).isEqualTo(role.getDescription());
        assertThat(response.getModifiedAt()).isEqualTo(role.getModifiedAt().toEpochDay());
        assertThat(response.getModifiedBy()).isEqualTo(role.getModifiedBy());
        assertThat(response.isEnabled()).isEqualTo(role.isEnabled());

        final List<String> expectedUsers = List.of();
        assertThat(response.getUsers()).isEqualTo(expectedUsers);
    }

    @Test
    public void testFromRoles_Success() {
        final List<Role> roles = IntStream.range(0, 5)
                .mapToObj(i -> generateRole())
                .collect(Collectors.toList());
        final List<RoleStructureResponse> responses = RoleStructureResponse.fromRoles(roles);

        assertThat(responses).isNotNull();
        assertThat(responses.size()).isEqualTo(roles.size());

        for (int i = 0; i < roles.size(); i++) {
            final Role role = roles.get(i);
            final RoleStructureResponse response = responses.get(i);

            assertThat(response.getIdRole()).isEqualTo(role.getIdRole());
            assertThat(response.getAuthority()).isEqualTo(role.getAuthority());
            assertThat(response.getDisplayName()).isEqualTo(role.getDisplayName());
            assertThat(response.getDescription()).isEqualTo(role.getDescription());
            assertThat(response.getModifiedAt()).isEqualTo(role.getModifiedAt().toEpochDay());
            assertThat(response.getModifiedBy()).isEqualTo(role.getModifiedBy());
            assertThat(response.isEnabled()).isEqualTo(role.isEnabled());

            final List<String> expectedUsers = role.getUsers().stream()
                    .map(user -> user.getFirstName() + " " + user.getLastName())
                    .collect(Collectors.toList());
            assertThat(response.getUsers()).isEqualTo(expectedUsers);
        }
    }

    @Test
    public void testFromRoles_RolesAreNull() {
        final List<RoleStructureResponse> responses = RoleStructureResponse.fromRoles(null);

        assertThat(responses).isEqualTo(List.of());
    }

    @Test
    public void testFromRoles_RolesNull() {
        final List<Role> roles = null;
        final List<RoleStructureResponse> responses = RoleStructureResponse.fromRoles(roles);

        assertThat(responses).isNotNull();
        assertThat(responses.size()).isEqualTo(0);
    }

    @Test
    public void testToString() {
        final Role role = generateRole();
        final RoleStructureResponse response = RoleStructureResponse.fromRole(role);

        final String exceptedUsersToString = role.getUsers().stream()
                .map(user -> user.getFirstName() + " " + user.getLastName())
                .collect(Collectors.joining(", "));
        final String exceptedToString = "RoleStructureResponse(idRole=" + role.getIdRole() +
                ", authority=" + role.getAuthority() +
                ", displayName=" + role.getDisplayName() +
                ", description=" + role.getDescription() +
                ", modifiedAt=" + role.getModifiedAt().toEpochDay() +
                ", modifiedBy=" + role.getModifiedBy() +
                ", enabled=" + role.isEnabled() +
                ", users=[" + exceptedUsersToString + "])";
        final String exceptedToStringLombok = "RoleStructureResponse.RoleStructureResponseBuilder(idRole=" + role.getIdRole() +
                ", authority=" + role.getAuthority() +
                ", displayName=" + role.getDisplayName() +
                ", description=" + role.getDescription() +
                ", modifiedAt=" + role.getModifiedAt().toEpochDay() +
                ", modifiedBy=" + role.getModifiedBy() +
                ", enabled=" + role.isEnabled() +
                ", users=[" + exceptedUsersToString + "])";

        assertThat(response).isNotNull();
        assertThat(response.toString()).isEqualTo(exceptedToString);
        assertThat(RoleStructureResponse.builder()
                .idRole(role.getIdRole())
                .authority(role.getAuthority())
                .displayName(role.getDisplayName())
                .description(role.getDescription())
                .modifiedAt(role.getModifiedAt().toEpochDay())
                .modifiedBy(role.getModifiedBy())
                .enabled(role.isEnabled())
                .users(role.getUsers().stream().map(user -> user.getFirstName() + " " + user.getLastName()).collect(Collectors.toList()))
                .toString()
        ).isEqualTo(exceptedToStringLombok);
    }

    private Role generateRole() {
        return Role.builder()
                .idRole((long) (Math.random() * 1000))
                .authority(RandomStringUtils.randomAlphanumeric(10))
                .displayName(RandomStringUtils.randomAlphanumeric(10))
                .description(RandomStringUtils.randomAlphanumeric(20))
                .modifiedAt(LocalDate.now())
                .modifiedBy(RandomStringUtils.randomAlphanumeric(10))
                .enabled(Math.random() < 0.5)
                .users(generateUsers())
                .build();
    }

    private List<User> generateUsers() {
        return IntStream.range(0, 5)
                .mapToObj(i -> User.builder()
                        .email(RandomStringUtils.randomAlphanumeric(10) + "@test.com")
                        .password(RandomStringUtils.randomAlphanumeric(30))
                        .firstName(RandomStringUtils.randomAlphanumeric(5))
                        .lastName(RandomStringUtils.randomAlphanumeric(7))
                        .build())
                .collect(Collectors.toList());
    }
}