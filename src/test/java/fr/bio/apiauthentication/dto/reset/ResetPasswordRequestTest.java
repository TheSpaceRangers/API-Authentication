package fr.bio.apiauthentication.dto.reset;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@DisplayName("Test reset password DTO request")
public class ResetPasswordRequestTest {
    private static final ObjectMapper mapper = new ObjectMapper();

    private ResetPasswordRequest request;

    private String password;

    @BeforeEach
    void setUp() {
        password = RandomStringUtils.randomAlphanumeric(30);

        request = new ResetPasswordRequest(password);
    }

    @AfterEach
    void tearDown() {
        request = null;
    }

    @Test
    public void testRecordFields() {
        assertThat(request).isNotNull();
        assertThat(request.password()).isEqualTo(password);
    }

    @Test
    public void testEquals() {
        ResetPasswordRequest requestEquals = new ResetPasswordRequest(password);

        assertThat(request).isEqualTo(requestEquals);
    }

    @Test
    public void testNotEquals() {
        ResetPasswordRequest requestEquals = new ResetPasswordRequest(
                RandomStringUtils.randomAlphanumeric(30)
        );

        assertThat(request).isNotEqualTo(requestEquals);
    }

    @Test
    public void testSerialize() throws Exception {
        String json = mapper.writeValueAsString(request);
        ResetPasswordRequest actualRequest = mapper.readValue(json, ResetPasswordRequest.class);

        String expectedJson = "{" +
                "\"password\":\"" + password + "\"" +
                "}";
        ResetPasswordRequest expectedRequest = mapper.readValue(expectedJson, ResetPasswordRequest.class);

        assertThat(actualRequest).isEqualTo(expectedRequest);
    }

    @Test
    public void testDeserialize() throws Exception {
        String json = "{" +
                "\"password\":\"" + password + "\"" +
                "}";

        ResetPasswordRequest requestMapped = mapper.readValue(json, ResetPasswordRequest.class);

        assertThat(requestMapped).usingRecursiveComparison().isEqualTo(request);
    }
}