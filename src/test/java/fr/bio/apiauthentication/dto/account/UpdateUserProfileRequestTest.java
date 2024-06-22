package fr.bio.apiauthentication.dto.account;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@DisplayName("Test update user profile DTO Request")
public class UpdateUserProfileRequestTest {
    private static final ObjectMapper mapper = new ObjectMapper();

    private UpdateUserProfilRequest request;

    @BeforeEach
    void setUp() {
        request = new UpdateUserProfilRequest(
                "John",
                "Doe",
                "j.doe@test.properties.com"
        );
    }

    @AfterEach
    void tearDown() {
        request = null;
    }

    @Test
    public void testRecordFields() {
        assertThat(request.email()).isEqualTo("j.doe@test.properties.com");
    }

    @Test
    public void testEquals() {
        UpdateUserProfilRequest requestEquals = new UpdateUserProfilRequest(
                "John",
                "Doe",
                "j.doe@test.properties.com"
        );

        assertThat(request).isEqualTo(requestEquals);
    }

    @Test
    public void testNotEquals() {
        UpdateUserProfilRequest requestNotEquals = new UpdateUserProfilRequest(
                "False",
                "False",
                "f.false@test.properties.com"
        );

        assertThat(request).isNotEqualTo(requestNotEquals);
    }

    @Test
    public void testSerialize() throws Exception {
        String json = mapper.writeValueAsString(request);
        UpdateUserProfilRequest actualRequest = mapper.readValue(json, UpdateUserProfilRequest.class);

        String expectedJson = "{" +
                "\"first_name\":\"John\"," +
                "\"last_name\":\"Doe\"," +
                "\"email\":\"j.doe@test.properties.com\"" +
                "}";
        UpdateUserProfilRequest expectedRequest = mapper.readValue(expectedJson, UpdateUserProfilRequest.class);

        assertThat(actualRequest).isEqualTo(expectedRequest);
    }

    @Test
    public void testDeserialize() throws Exception {
        String json = "{" +
                "\"first_name\":\"John\"," +
                "\"last_name\":\"Doe\"," +
                "\"email\":\"j.doe@test.properties.com\"" +
                "}";
        UpdateUserProfilRequest requestMapped = mapper.readValue(json, UpdateUserProfilRequest.class);

        assertThat(requestMapped).usingRecursiveComparison().isEqualTo(request);
    }
}
