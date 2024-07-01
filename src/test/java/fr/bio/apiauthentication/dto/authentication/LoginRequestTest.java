package fr.bio.apiauthentication.dto.authentication;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@DisplayName("Test Authentication DTO Request")
public class LoginRequestTest {
    private static final ObjectMapper mapper = new ObjectMapper();

    private LoginRequest request;

    @BeforeEach
    void setUp() {
        request = new LoginRequest(
                "username@test.properties.com",
                "password"
        );
    }

    @AfterEach
    void tearDown() {
        request = null;
    }

    @Test
    public void testRecordFields() {
        assertThat(request.email()).isEqualTo("username@test.properties.com");
        assertThat(request.password()).isEqualTo("password");
    }

    @Test
    public void testEquals() {
        LoginRequest requestEquals = new LoginRequest(
                "username@test.properties.com",
                "password"
        );

        assertThat(request).isEqualTo(requestEquals);
    }

    @Test
    public void testNotEquals() {
        LoginRequest requestEquals = new LoginRequest(
                "username@test.properties.com",
                "password not equals"
        );

        assertThat(request).isNotEqualTo(requestEquals);
    }

    @Test
    public void testSerialize() throws Exception {
        String json = mapper.writeValueAsString(request);
        LoginRequest actualRequest = mapper.readValue(json, LoginRequest.class);

        String expectedJson = "{" +
                "\"email\":\"username@test.properties.com\"," +
                "\"password\":\"password\"" +
                "}";
        LoginRequest expectedRequest = mapper.readValue(expectedJson, LoginRequest.class);

        assertThat(actualRequest).isEqualTo(expectedRequest);
    }

    @Test
    public void testDeserialize() throws Exception {
        String json = "{" +
                "\"email\":\"username@test.properties.com\"," +
                "\"password\":\"password\"" +
                "}";

        LoginRequest requestMapped = mapper.readValue(json, LoginRequest.class);

        assertThat(requestMapped).usingRecursiveComparison().isEqualTo(request);
    }
}
