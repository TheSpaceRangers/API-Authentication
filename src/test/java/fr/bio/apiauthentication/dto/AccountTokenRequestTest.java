package fr.bio.apiauthentication.dto;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@DisplayName("Test get user profile DTO Request")
public class AccountTokenRequestTest {
    private static final ObjectMapper mapper = new ObjectMapper();

    private AccountTokenRequest request;

    @BeforeEach
    void setUp() {
        request = new AccountTokenRequest("This is a token test.properties");
    }

    @AfterEach
    void tearDown() {
        request = null;
    }

    @Test
    public void testRecordFields() {
        assertThat(request.token()).isEqualTo("This is a token test.properties");
    }

    @Test
    public void testEquals() {
        AccountTokenRequest requestEquals = new AccountTokenRequest("This is a token test.properties");

        assertThat(request).isEqualTo(requestEquals);
    }

    @Test
    public void testNotEquals() {
        AccountTokenRequest requestNotEquals = new AccountTokenRequest("This is a token test.properties false");

        assertThat(request).isNotEqualTo(requestNotEquals);
    }

    @Test
    public void testSerialize() throws Exception {
        String json = mapper.writeValueAsString(request);
        AccountTokenRequest actualRequest = mapper.readValue(json, AccountTokenRequest.class);

        String expectedJson = "{\"token\":\"This is a token test.properties\"}";
        AccountTokenRequest expectedRequest = mapper.readValue(expectedJson, AccountTokenRequest.class);

        assertThat(expectedRequest).isEqualTo(actualRequest);
    }

    @Test
    public void testDeserialize() throws Exception {
        String json = "{\"token\":\"This is a token test.properties\"}";

        AccountTokenRequest requestMapped = mapper.readValue(json, AccountTokenRequest.class);

        assertThat(requestMapped).usingRecursiveComparison().isEqualTo(request);
    }
}
