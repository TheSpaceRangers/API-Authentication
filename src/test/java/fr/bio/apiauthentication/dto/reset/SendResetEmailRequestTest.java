package fr.bio.apiauthentication.dto.reset;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@DisplayName("Test send reset email DTO request")
public class SendResetEmailRequestTest {
    private static final ObjectMapper mapper = new ObjectMapper();

    private SendResetEmailRequest request;

    private String email;

    @BeforeEach
    void setUp() {
        email = RandomStringUtils.randomAlphanumeric(10) + "@test.com";

        request = new SendResetEmailRequest(email);
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
        SendResetEmailRequest requestEquals = new SendResetEmailRequest(email);

        assertThat(request).isEqualTo(requestEquals);
    }

    @Test
    public void testNotEquals() {
        SendResetEmailRequest requestEquals = new SendResetEmailRequest(
                RandomStringUtils.randomAlphanumeric(30)
        );

        assertThat(request).isNotEqualTo(requestEquals);
    }

    @Test
    public void testSerialize() throws Exception {
        String json = mapper.writeValueAsString(request);
        SendResetEmailRequest actualRequest = mapper.readValue(json, SendResetEmailRequest.class);

        String expectedJson = "{" +
                "\"email\":\"" + email + "\"" +
                "}";
        SendResetEmailRequest expectedRequest = mapper.readValue(expectedJson, SendResetEmailRequest.class);

        assertThat(actualRequest).isEqualTo(expectedRequest);
    }

    @Test
    public void testDeserialize() throws Exception {
        String json = "{" +
                "\"email\":\"" + email + "\"" +
                "}";

        SendResetEmailRequest requestMapped = mapper.readValue(json, SendResetEmailRequest.class);

        assertThat(requestMapped).usingRecursiveComparison().isEqualTo(request);
    }
}