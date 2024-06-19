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
                "This is a token test",
                "John",
                "Doe",
                "j.doe@test.com"
        );
    }

    @AfterEach
    void tearDown() {
        request = null;
    }

    @Test
    public void testRecordFields() {
        assertThat(request.token()).isEqualTo("This is a token test");
    }

    @Test
    public void testEquals() {
        UpdateUserProfilRequest requestEquals = new UpdateUserProfilRequest(
                "This is a token test",
                "John",
                "Doe",
                "j.doe@test.com"
        );

        assertThat(request).isEqualTo(requestEquals);
    }

    @Test
    public void testNotEquals() {
        UpdateUserProfilRequest requestNotEquals = new UpdateUserProfilRequest(
                "This is a token test false",
                "False",
                "flase",
                "F.f@test.com"
        );

        assertThat(request).isNotEqualTo(requestNotEquals);
    }

    @Test
    public void testSerialize() throws Exception {
        String json = mapper.writeValueAsString(request);
        String expectedJson = "{" +
                "\"token\":\"This is a token test\"," +
                "\"firstName\":\"John\"," +
                "\"lastName\":\"Doe\"," +
                "\"email\":\"j.doe@test.com\"" +
                "}";

        assertThat(json).isEqualTo(expectedJson);
    }

    @Test
    public void testDeserialize() throws Exception {
        String json = "{" +
                "\"token\":\"This is a token test\"," +
                "\"firstName\":\"John\"," +
                "\"lastName\":\"Doe\"," +
                "\"email\":\"j.doe@test.com\"" +
                "}";
        UpdateUserProfilRequest requestMapped = mapper.readValue(json, UpdateUserProfilRequest.class);

        assertThat(requestMapped).usingRecursiveComparison().isEqualTo(request);
    }
}
