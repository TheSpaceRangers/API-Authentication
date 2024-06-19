package fr.bio.apiauthentication.dto.account;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@DisplayName("Test get user profile DTO Request")
public class UserProfilRequestTest {
    private static final ObjectMapper mapper = new ObjectMapper();

    private UserProfilRequest request;

    @BeforeEach
    void setUp() {
        request = new UserProfilRequest("This is a token test");
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
        UserProfilRequest requestEquals = new UserProfilRequest("This is a token test");

        assertThat(request).isEqualTo(requestEquals);
    }

    @Test
    public void testNotEquals() {
        UserProfilRequest requestNotEquals = new UserProfilRequest("This is a token test false");

        assertThat(request).isNotEqualTo(requestNotEquals);
    }

    @Test
    public void testSerialize() throws Exception {
        String json = mapper.writeValueAsString(request);
        String expectedJson = "{\"token\":\"This is a token test\"}";

        assertThat(json).isEqualTo(expectedJson);
    }

    @Test
    public void testDeserialize() throws Exception {
        String json = "{\"token\":\"This is a token test\"}";

        UserProfilRequest requestMapped = mapper.readValue(json, UserProfilRequest.class);

        assertThat(requestMapped).usingRecursiveComparison().isEqualTo(request);
    }
}
