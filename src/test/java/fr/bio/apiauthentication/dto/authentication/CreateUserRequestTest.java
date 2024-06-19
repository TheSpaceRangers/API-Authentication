package fr.bio.apiauthentication.dto.authentication;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@DisplayName("Test User DTO New")
public class CreateUserRequestTest {
    private static final ObjectMapper mapper = new ObjectMapper();

    private CreateUserRequest request;

    @BeforeEach
    void setUp() {
        request = new CreateUserRequest(
                "Charles",
                "Tronel",
                "c.tronel@test.com",
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
        assertThat(request.email()).isEqualTo("c.tronel@test.com");
        assertThat(request.password()).isEqualTo("12345678");
    }

    @Test
    public void testEquals() {
        CreateUserRequest requestEquals = new CreateUserRequest(
                "Charles",
                "Tronel",
                "c.tronel@test.com",
                "12345678"
        );

        assertThat(request).isEqualTo(requestEquals);
    }

    @Test
    public void testNotEquals() {
        CreateUserRequest requestNotEquals = new CreateUserRequest(
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
        String expectedJson = "{" +
                "\"firstName\":\"Charles\"," +
                "\"lastName\":\"Tronel\"," +
                "\"email\":\"c.tronel@test.com\"," +
                "\"password\":\"12345678\"" +
                "}";

        assertThat(expectedJson).isEqualTo(json);
    }

    @Test
    public void testDeserialize() throws Exception {
        String json = "{" +
                "\"firstName\":\"Charles\"," +
                "\"lastName\":\"Tronel\"," +
                "\"email\":\"c.tronel@test.com\"," +
                "\"password\":\"12345678\"" +
                "}";

        CreateUserRequest request = mapper.readValue(json, CreateUserRequest.class);

        assertThat(request).usingRecursiveComparison().isEqualTo(request);
    }
}