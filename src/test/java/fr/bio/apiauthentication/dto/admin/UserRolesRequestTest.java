package fr.bio.apiauthentication.dto.admin;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.stream.IntStream;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@DisplayName("Test user roles request DTO")
public class UserRolesRequestTest {
    private static final ObjectMapper mapper = new ObjectMapper();

    private UserRolesRequest request;

    private String email;
    private List<String> rolesToAdd;
    private List<String> rolesToRemove;

    @BeforeEach
    void setUp() {
        email = RandomStringUtils.randomAlphabetic(10) + "@test.com";
        rolesToAdd = IntStream.range(0, 5)
                .mapToObj(role -> RandomStringUtils.randomAlphabetic(10).toUpperCase())
                .toList();
        rolesToRemove = IntStream.range(0, 5)
                .mapToObj(role -> RandomStringUtils.randomAlphanumeric(10).toUpperCase())
                .toList();

        request = new UserRolesRequest(email, rolesToAdd, rolesToRemove);
    }

    @AfterEach
    void tearDown() {
        request = null;
    }

    @Test
    public void testRecordFields() {
        assertThat(request).isNotNull();
        assertThat(request.email()).isEqualTo(email);
        assertThat(request.rolesToAdd()).isEqualTo(rolesToAdd);
        assertThat(request.rolesToRemove()).isEqualTo(rolesToRemove);
    }

    @Test
    public void testEqualsAndHashCode() {
        final UserRolesRequest exceptedRequest = new UserRolesRequest(email, rolesToAdd, rolesToRemove);

        assertThat(request).isEqualTo(exceptedRequest);
        assertThat(request.hashCode()).isEqualTo(exceptedRequest.hashCode());
    }

    @Test
    public void testNotEqualsAndHashCode() {
        final UserRolesRequest exceptedRequest = new UserRolesRequest(
                RandomStringUtils.randomAlphabetic(10) + "@test.com",
                IntStream.range(0, 5)
                        .mapToObj(role -> RandomStringUtils.randomAlphabetic(10).toUpperCase())
                        .toList(),
                IntStream.range(0, 5)
                        .mapToObj(role -> RandomStringUtils.randomAlphanumeric(10).toUpperCase())
                        .toList()
                );

        assertThat(request).isNotEqualTo(exceptedRequest);
        assertThat(request.hashCode()).isNotEqualTo(exceptedRequest.hashCode());
    }

    @Test
    public void testSerialize() throws Exception {
        final String json = mapper.writeValueAsString(request);
        final UserRolesRequest actualRequest = mapper.readValue(json, UserRolesRequest.class);

        final String expectedJson = "{" +
                "\"email\":\"" + email + "\"," +
                "\"rolesToAdd\":[" + String.join(",", request.rolesToAdd().stream().map(role -> "\"" + role + "\"").toList()) + "]," +
                "\"rolesToRemove\":[" + String.join(",", request.rolesToRemove().stream().map(role -> "\"" + role + "\"").toList()) + "]" +
                "}";
        final UserRolesRequest expectedRequest = mapper.readValue(expectedJson, UserRolesRequest.class);

        assertThat(actualRequest).isEqualTo(expectedRequest);
    }

    @Test
    public void testDeserialize() throws Exception {
        final String json = "{" +
                "\"email\":\"" + email + "\"," +
                "\"rolesToAdd\":[" + String.join(",", request.rolesToAdd().stream().map(role -> "\"" + role + "\"").toList()) + "]," +
                "\"rolesToRemove\":[" + String.join(",", request.rolesToRemove().stream().map(role -> "\"" + role + "\"").toList()) + "]" +
                "}";
        final UserRolesRequest requestMapped = mapper.readValue(json, UserRolesRequest.class);

        assertThat(requestMapped).usingRecursiveComparison().isEqualTo(request);
    }
}