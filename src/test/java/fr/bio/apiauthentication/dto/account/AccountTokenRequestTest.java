package fr.bio.apiauthentication.dto.account;

import com.fasterxml.jackson.databind.ObjectMapper;
import fr.bio.apiauthentication.dto.AccountTokenRequest;
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
        request = new AccountTokenRequest("This is a token test");
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
        AccountTokenRequest requestEquals = new AccountTokenRequest("This is a token test");

        assertThat(request).isEqualTo(requestEquals);
    }

    @Test
    public void testNotEquals() {
        AccountTokenRequest requestNotEquals = new AccountTokenRequest("This is a token test false");

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

        AccountTokenRequest requestMapped = mapper.readValue(json, AccountTokenRequest.class);

        assertThat(requestMapped).usingRecursiveComparison().isEqualTo(request);
    }
}
