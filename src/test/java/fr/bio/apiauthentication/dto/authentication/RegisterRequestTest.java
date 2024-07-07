package fr.bio.apiauthentication.dto.authentication;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@DisplayName("Test User DTO New")
public class RegisterRequestTest {
    private static final ObjectMapper mapper = new ObjectMapper();

    private RegisterRequest request;

    private String email;
    private String password;
    private String firstName;
    private String lastName;

    @BeforeEach
    void setUp() {
        email = RandomStringUtils.randomAlphanumeric(10) + "@test.com";
        password = RandomStringUtils.randomAlphanumeric(30);
        firstName = RandomStringUtils.randomAlphanumeric(20);
        lastName = RandomStringUtils.randomAlphanumeric(20);

        request = new RegisterRequest(firstName, lastName, email, password);
    }

    @AfterEach
    void tearDown() {
        request = null;
    }

    @Test
    public void testRecordFields() {
        assertThat(request).isNotNull();
        assertThat(request.email()).isEqualTo(email);
        assertThat(request.password()).isEqualTo(password);
        assertThat(request.firstName()).isEqualTo(firstName);
        assertThat(request.lastName()).isEqualTo(lastName);
    }

    @Test
    public void testEquals() {
        RegisterRequest requestEquals = new RegisterRequest(firstName, lastName, email, password);

        assertThat(request).isEqualTo(requestEquals);
    }

    @Test
    public void testNotEquals() {
        RegisterRequest requestNotEquals = new RegisterRequest(
                RandomStringUtils.randomAlphanumeric(20),
                RandomStringUtils.randomAlphanumeric(20),
                RandomStringUtils.randomAlphanumeric(10) + "@test.com",
                RandomStringUtils.randomAlphanumeric(30)
        );

        assertThat(request).isNotEqualTo(requestNotEquals);
    }

    @Test
    public void testSerialize() throws Exception {
        String json = mapper.writeValueAsString(request);
        RegisterRequest actualRequest = mapper.readValue(json, RegisterRequest.class);

        String expectedJson = "{" +
                "\"firstName\":\"" + firstName + "\"," +
                "\"lastName\":\"" + lastName + "\"," +
                "\"email\":\"" + email + "\"," +
                "\"password\":\"" + password + "\"" +
                "}";
        RegisterRequest expectedRequest = mapper.readValue(expectedJson, RegisterRequest.class);

        assertThat(expectedRequest).isEqualTo(actualRequest);
    }

    @Test
    public void testDeserialize() throws Exception {
        String json = "{" +
                "\"firstName\":\"" + firstName + "\"," +
                "\"lastName\":\"" + lastName + "\"," +
                "\"email\":\"" + email + "\"," +
                "\"password\":\"" + password + "\"" +
                "}";

        RegisterRequest request = mapper.readValue(json, RegisterRequest.class);

        assertThat(request).usingRecursiveComparison().isEqualTo(request);
    }
}