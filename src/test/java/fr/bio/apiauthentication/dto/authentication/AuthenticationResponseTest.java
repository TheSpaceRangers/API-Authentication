package fr.bio.apiauthentication.dto.authentication;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@DisplayName("Test User DTO New")
public class AuthenticationResponseTest {
    private static final ObjectMapper mapper = new ObjectMapper();

    private AuthenticationResponse response;

    @BeforeEach
    void setUp() {
        response = new AuthenticationResponse(
                "Response message"
        );
    }

    @AfterEach
    void tearDown() {
        response = null;
    }

    @Test
    public void testRecordFields() {
        assertThat(response.getMessage()).isEqualTo("Response message");
    }

    @Test
    public void testEquals() {
        AuthenticationResponse responseEquals = new AuthenticationResponse(
                "Response message"
        );

        assertThat(response).isEqualTo(responseEquals);
    }

    @Test
    public void testNotEquals() {
        AuthenticationResponse responseNotEquals = new AuthenticationResponse(
                "Response "
        );

        assertThat(response).isNotEqualTo(responseNotEquals);
    }

    @Test
    public void testSerialize() throws Exception {
        String json = mapper.writeValueAsString(response);
        AuthenticationResponse actualResponse = mapper.readValue(json, AuthenticationResponse.class);

        String expectedJson = "{\"message\":\"Response message\"}";
        AuthenticationResponse expectedResponse = mapper.readValue(expectedJson, AuthenticationResponse.class);

        assertThat(actualResponse).isEqualTo(expectedResponse);
    }

    @Test
    public void testDeserialize() throws Exception {
        String json = "{\"message\":\"Response message\"}";

        AuthenticationResponse response = mapper.readValue(json, AuthenticationResponse.class);

        assertThat(response.getMessage()).isEqualTo("Response message");
    }
}