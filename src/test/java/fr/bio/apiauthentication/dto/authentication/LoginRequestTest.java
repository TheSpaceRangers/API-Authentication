package fr.bio.apiauthentication.dto.authentication;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@DisplayName("Test Authentication DTO Request")
public class LoginRequestTest {
    private static final ObjectMapper mapper = new ObjectMapper();

    private LoginRequest request;

    private String email;
    private String password;

    @BeforeEach
    void setUp() {
        email = RandomStringUtils.randomAlphanumeric(10) + "@test.com";
        password = RandomStringUtils.randomAlphanumeric(30);

        request = new LoginRequest(email, password);
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
    }

    @Test
    public void testEquals() {
        LoginRequest requestEquals = new LoginRequest(email, password);

        assertThat(request).isEqualTo(requestEquals);
    }

    @Test
    public void testNotEquals() {
        LoginRequest requestEquals = new LoginRequest(
                RandomStringUtils.randomAlphanumeric(10) + "@test.com",
                RandomStringUtils.randomAlphanumeric(30)
        );

        assertThat(request).isNotEqualTo(requestEquals);
    }

    @Test
    public void testSerialize() throws Exception {
        String json = mapper.writeValueAsString(request);
        LoginRequest actualRequest = mapper.readValue(json, LoginRequest.class);

        String expectedJson = "{" +
                "\"email\":\"" + email +"\"," +
                "\"password\":\"" + password + "\"" +
                "}";
        LoginRequest expectedRequest = mapper.readValue(expectedJson, LoginRequest.class);

        assertThat(actualRequest).isEqualTo(expectedRequest);
    }

    @Test
    public void testDeserialize() throws Exception {
        String json = "{" +
                "\"email\":\"" + email +"\"," +
                "\"password\":\"" + password + "\"" +
                "}";

        LoginRequest requestMapped = mapper.readValue(json, LoginRequest.class);

        assertThat(requestMapped).usingRecursiveComparison().isEqualTo(request);
    }
}