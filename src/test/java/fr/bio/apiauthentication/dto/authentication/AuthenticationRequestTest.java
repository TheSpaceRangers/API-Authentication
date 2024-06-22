package fr.bio.apiauthentication.dto.authentication;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@DisplayName("Test Authentication DTO Request")
public class AuthenticationRequestTest {
    private static final ObjectMapper mapper = new ObjectMapper();

    private AuthenticationRequest request;

    @BeforeEach
    void setUp() {
        request = new AuthenticationRequest(
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
        AuthenticationRequest requestEquals = new AuthenticationRequest(
                "username@test.properties.com",
                "password"
        );

        assertThat(request).isEqualTo(requestEquals);
    }

    @Test
    public void testNotEquals() {
        AuthenticationRequest requestEquals = new AuthenticationRequest(
                "username@test.properties.com",
                "password not equals"
        );

        assertThat(request).isNotEqualTo(requestEquals);
    }

    @Test
    public void testSerialize() throws Exception {
        String json = mapper.writeValueAsString(request);
        AuthenticationRequest actualRequest = mapper.readValue(json, AuthenticationRequest.class);

        String expectedJson = "{" +
                "\"email\":\"username@test.properties.com\"," +
                "\"password\":\"password\"" +
                "}";
        AuthenticationRequest expectedRequest = mapper.readValue(expectedJson, AuthenticationRequest.class);

        assertThat(actualRequest).isEqualTo(expectedRequest);
    }

    @Test
    public void testDeserialize() throws Exception {
        String json = "{" +
                "\"email\":\"username@test.properties.com\"," +
                "\"password\":\"password\"" +
                "}";

        AuthenticationRequest requestMapped = mapper.readValue(json, AuthenticationRequest.class);

        assertThat(requestMapped).usingRecursiveComparison().isEqualTo(request);
    }
}
