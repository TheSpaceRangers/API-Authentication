package fr.bio.apiauthentication.dto.authentication;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@DisplayName("Test User DTO New")
public class RegisterRequestTest {
    private static final ObjectMapper mapper = new ObjectMapper();

    private RegisterRequest request;

    @BeforeEach
    void setUp() {
        request = new RegisterRequest(
                "Charles",
                "Tronel",
                "c.tronel@test.properties.com",
                "12345678"
        );
    }

    @AfterEach
    void tearDown() {
        request = null;
    }

    @Test
    public void testRecordFields() {
        assertThat(request.firstName()).isEqualTo("Charles");
        assertThat(request.lastName()).isEqualTo("Tronel");
        assertThat(request.email()).isEqualTo("c.tronel@test.properties.com");
        assertThat(request.password()).isEqualTo("12345678");
    }

    @Test
    public void testEquals() {
        RegisterRequest requestEquals = new RegisterRequest(
                "Charles",
                "Tronel",
                "c.tronel@test.properties.com",
                "12345678"
        );

        assertThat(request).isEqualTo(requestEquals);
    }

    @Test
    public void testNotEquals() {
        RegisterRequest requestNotEquals = new RegisterRequest(
                "John",
                "Doe",
                "john.doe@example.com",
                "12345678"
        );

        assertThat(request).isNotEqualTo(requestNotEquals);
    }

    @Test
    public void testSerialize() throws Exception {
        String json = mapper.writeValueAsString(request);
        RegisterRequest actualRequest = mapper.readValue(json, RegisterRequest.class);

        String expectedJson = "{" +
                "\"firstName\":\"Charles\"," +
                "\"lastName\":\"Tronel\"," +
                "\"email\":\"c.tronel@test.properties.com\"," +
                "\"password\":\"12345678\"" +
                "}";
        RegisterRequest expectedRequest = mapper.readValue(expectedJson, RegisterRequest.class);

        assertThat(expectedRequest).isEqualTo(actualRequest);
    }

    @Test
    public void testDeserialize() throws Exception {
        String json = "{" +
                "\"firstName\":\"Charles\"," +
                "\"lastName\":\"Tronel\"," +
                "\"email\":\"c.tronel@test.properties.com\"," +
                "\"password\":\"12345678\"" +
                "}";

        RegisterRequest request = mapper.readValue(json, RegisterRequest.class);

        assertThat(request).usingRecursiveComparison().isEqualTo(request);
    }
}