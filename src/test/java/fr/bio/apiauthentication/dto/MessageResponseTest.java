package fr.bio.apiauthentication.dto;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@DisplayName("Test message DTO Response")
public class MessageResponseTest {
    private static final ObjectMapper mapper = new ObjectMapper();

    private MessageResponse response;

    @BeforeEach
    void setUp() {
        response = new MessageResponse("This is a test message");
    }

    @AfterEach
    void tearDown() {
        response = null;
    }

    @Test
    public void testRecordFields() {
        assertThat(response.getMessage()).isEqualTo("This is a test message");
    }

    @Test
    public void testEquals() {
        MessageResponse requestEquals = new MessageResponse("This is a test message");;

        assertThat(response).isEqualTo(requestEquals);
    }

    @Test
    public void testNotEquals() {
        MessageResponse requestNotEquals = new MessageResponse("This is a different message");

        assertThat(response).isNotEqualTo(requestNotEquals);
    }

    @Test
    public void testSerialize() throws Exception {
        String json = mapper.writeValueAsString(response);
        MessageResponse actualRequest = mapper.readValue(json, MessageResponse.class);

        String expectedJson = "{\"message\":\"This is a test message\"}";
        MessageResponse expectedRequest = mapper.readValue(expectedJson, MessageResponse.class);

        assertThat(expectedRequest).isEqualTo(actualRequest);
    }

    @Test
    public void testDeserialize() throws Exception {
        String json = "{\"message\":\"This is a test message\"}";

        MessageResponse requestMapped = mapper.readValue(json, MessageResponse.class);

        assertThat(requestMapped).usingRecursiveComparison().isEqualTo(response);
    }
}
