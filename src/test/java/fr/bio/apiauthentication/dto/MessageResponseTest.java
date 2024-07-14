package fr.bio.apiauthentication.dto;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@DisplayName("Test message DTO Response")
public class MessageResponseTest {
    private static final ObjectMapper mapper = new ObjectMapper();

    private MessageResponse response;

    private String message;

    @BeforeEach
    void setUp() {
        message = RandomStringUtils.randomAlphabetic(100);

        response = new MessageResponse(message);
    }

    @AfterEach
    void tearDown() {
        response = null;
    }

    @Test
    public void testRecordFields() {
        assertThat(response).isNotNull();
        assertThat(response.getMessage()).isEqualTo(message);
    }

    @Test
    public void testEqualsAndHashCode() {
        MessageResponse requestEquals = new MessageResponse(message);

        assertThat(response).isEqualTo(requestEquals);
        assertThat(response).hasSameHashCodeAs(requestEquals);
    }

    @Test
    public void testNotEqualsAndHashCode() {
        MessageResponse requestNotEquals = new MessageResponse(RandomStringUtils.randomAlphabetic(100));

        assertThat(response).isNotEqualTo(requestNotEquals);
        assertThat(response).doesNotHaveSameHashCodeAs(requestNotEquals);
    }

    @Test
    public void testSerialize() throws Exception {
        String json = mapper.writeValueAsString(response);
        MessageResponse actualRequest = mapper.readValue(json, MessageResponse.class);

        String expectedJson = "{\"message\":\"" + message + "\"}";
        MessageResponse expectedRequest = mapper.readValue(expectedJson, MessageResponse.class);

        assertThat(expectedRequest).isEqualTo(actualRequest);
    }

    @Test
    public void testDeserialize() throws Exception {
        String json = "{\"message\":\"" + message + "\"}";

        MessageResponse requestMapped = mapper.readValue(json, MessageResponse.class);

        assertThat(requestMapped).usingRecursiveComparison().isEqualTo(response);
    }

    @Test
    public void testFromMessage() {
        MessageResponse response = MessageResponse.fromMessage(message);

        assertThat(response.getMessage()).isEqualTo(message);
    }

    @Test
    public void testToString() {
        final MessageResponse response = new MessageResponse(message);

        final String exceptedToString = "MessageResponse(message=%s)".formatted(message);
        final String exceptedToStringLombok = "MessageResponse.MessageResponseBuilder(message=%s)".formatted(message);

        assertThat(response).isNotNull();
        assertThat(response.toString()).isEqualTo(exceptedToString);
        assertThat(MessageResponse.builder()
                .message(message)
                .toString()
        ).isEqualTo(exceptedToStringLombok);
    }
}