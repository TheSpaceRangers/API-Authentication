package fr.bio.apiauthentication.dto.account;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@DisplayName("Test update user profile DTO Request")
public class UpdateUserProfileRequestTest {
    private static final ObjectMapper mapper = new ObjectMapper();

    private UpdateUserProfilRequest request;

    private String firstName;
    private String lastName;
    private String email;

    @BeforeEach
    void setUp() {
        firstName = RandomStringUtils.randomAlphanumeric(20);
        lastName = RandomStringUtils.randomAlphanumeric(20);
        email = RandomStringUtils.randomAlphanumeric(10) + "@test.com";

        request = new UpdateUserProfilRequest(
                firstName,
                lastName,
                email
        );
    }

    @AfterEach
    void tearDown() {
        request = null;
    }

    @Test
    public void testRecordFields() {
        assertThat(request.firstName()).isEqualTo(firstName);
        assertThat(request.lastName()).isEqualTo(lastName);
        assertThat(request.email()).isEqualTo(email);
    }

    @Test
    public void testEquals() {
        UpdateUserProfilRequest requestEquals = new UpdateUserProfilRequest(
                firstName,
                lastName,
                email
        );

        assertThat(request).isEqualTo(requestEquals);
    }

    @Test
    public void testNotEquals() {
        UpdateUserProfilRequest requestNotEquals = new UpdateUserProfilRequest(
                RandomStringUtils.randomAlphanumeric(20),
                RandomStringUtils.randomAlphanumeric(20),
                RandomStringUtils.randomAlphanumeric(10) + "@test.com"
        );

        assertThat(request).isNotEqualTo(requestNotEquals);
    }

    @Test
    public void testSerialize() throws Exception {
        String json = mapper.writeValueAsString(request);
        UpdateUserProfilRequest actualRequest = mapper.readValue(json, UpdateUserProfilRequest.class);

        String expectedJson = "{" +
                "\"first_name\":\"" + firstName + "\"," +
                "\"last_name\":\"" + lastName + "\"," +
                "\"email\":\"" + email + "\"" +
                "}";
        UpdateUserProfilRequest expectedRequest = mapper.readValue(expectedJson, UpdateUserProfilRequest.class);

        assertThat(actualRequest).isEqualTo(expectedRequest);
    }

    @Test
    public void testDeserialize() throws Exception {
        String json = "{" +
                "\"first_name\":\"" + firstName + "\"," +
                "\"last_name\":\"" + lastName + "\"," +
                "\"email\":\"" + email + "\"" +
                "}";
        UpdateUserProfilRequest requestMapped = mapper.readValue(json, UpdateUserProfilRequest.class);

        assertThat(requestMapped).usingRecursiveComparison().isEqualTo(request);
    }
}