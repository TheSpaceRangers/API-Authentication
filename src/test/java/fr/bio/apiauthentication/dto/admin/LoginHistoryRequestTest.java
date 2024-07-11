package fr.bio.apiauthentication.dto.admin;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@DisplayName("Test login history request DTO")
public class LoginHistoryRequestTest {
    private static final ObjectMapper mapper = new ObjectMapper();

    private LoginHistoryRequest request;

    private String email;

    @BeforeEach
    void setUp() {
        email = RandomStringUtils.randomAlphanumeric(10) + "@test.com";

        request = new LoginHistoryRequest(email);
    }

    @AfterEach
    void tearDown() {
        request = null;
    }

    @Test
    public void testRecordFields() {
        assertThat(request).isNotNull();
        assertThat(request.email()).isEqualTo(email);
    }

    @Test
    public void testEquals() {
        LoginHistoryRequest requestEquals = new LoginHistoryRequest(email);

        assertThat(request).isEqualTo(requestEquals);
    }

    @Test
    public void testNotEquals() {
        LoginHistoryRequest requestNotEquals = new LoginHistoryRequest(
                RandomStringUtils.randomAlphanumeric(20) + "@test.com"
        );

        assertThat(request).isNotEqualTo(requestNotEquals);
    }

    @Test
    public void testSerialize() throws Exception {
        String json = mapper.writeValueAsString(request);
        LoginHistoryRequest actualRequest = mapper.readValue(json, LoginHistoryRequest.class);

        String expectedJson = "{" +
                "\"email\":\"" + email + "\"" +
                "}";
        LoginHistoryRequest expectedRequest = mapper.readValue(expectedJson, LoginHistoryRequest.class);

        assertThat(actualRequest).isEqualTo(expectedRequest);
    }

    @Test
    public void testDeserialize() throws Exception {
        String json = "{" +
                "\"email\":\"" + email + "\"" +
                "}";
        LoginHistoryRequest requestMapped = mapper.readValue(json, LoginHistoryRequest.class);

        assertThat(requestMapped).usingRecursiveComparison().isEqualTo(request);
    }
}