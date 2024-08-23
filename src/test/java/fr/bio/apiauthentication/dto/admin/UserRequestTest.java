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

@DisplayName("Test role request DTO")
public class UserRequestTest {
    private static final ObjectMapper mapper = new ObjectMapper();

    private UserRequest request;

    private String email;
    private String firstName;
    private String lastName;
    private List<String> roles;

    @BeforeEach
    void setUp() {
        email = RandomStringUtils.randomAlphabetic(10) + "@test.com";
        firstName = RandomStringUtils.randomAlphabetic(20);
        lastName = RandomStringUtils.randomAlphabetic(20);
        roles = IntStream.range(0, 5).mapToObj(user -> RandomStringUtils.randomAlphabetic(10).toUpperCase()).toList();

        request = new UserRequest(email, firstName, lastName, roles);
    }

    @AfterEach
    void tearDown() {
        request = null;
    }

    @Test
    public void testRecordFields() {
        assertThat(request).isNotNull();
        assertThat(request.email()).isEqualTo(email);
        assertThat(request.firstName()).isEqualTo(firstName);
        assertThat(request.lastName()).isEqualTo(lastName);
        assertThat(request.roles()).isEqualTo(roles);
    }

    @Test
    public void testEquals() {
        UserRequest requestEquals = new UserRequest(email, firstName, lastName, roles);

        assertThat(request).isEqualTo(requestEquals);
    }

    @Test
    public void testNotEquals() {
        UserRequest requestNotEquals = new UserRequest(
                RandomStringUtils.randomAlphabetic(10) + "@test.com",
                RandomStringUtils.randomAlphabetic(20),
                RandomStringUtils.randomAlphabetic(20),
                List.of(RandomStringUtils.randomAlphanumeric(5).toUpperCase())
        );

        assertThat(request).isNotEqualTo(requestNotEquals);
    }

    @Test
    public void testSerialize() throws Exception {
        String json = mapper.writeValueAsString(request);
        UserRequest actualRequest = mapper.readValue(json, UserRequest.class);

        String expectedJson = "{" +
                "\"email\":\"" + email + "\"," +
                "\"first_name\":\"" + firstName + "\"," +
                "\"last_name\":\"" + lastName + "\"," +
                "\"roles\":[" + String.join(",", request.roles().stream().map(role -> "\"" + role + "\"").toList()) + "]" +
                "}";
        UserRequest expectedRequest = mapper.readValue(expectedJson, UserRequest.class);

        assertThat(actualRequest).isEqualTo(expectedRequest);
    }

    @Test
    public void testDeserialize() throws Exception {
        String json = "{" +
                "\"email\":\"" + email + "\"," +
                "\"first_name\":\"" + firstName + "\"," +
                "\"last_name\":\"" + lastName + "\"," +
                "\"roles\":[" + String.join(",", request.roles().stream().map(role -> "\"" + role + "\"").toList()) + "]" +
                "}";
        UserRequest requestMapped = mapper.readValue(json, UserRequest.class);

        assertThat(requestMapped).usingRecursiveComparison().isEqualTo(request);
    }
}
