package fr.bio.apiauthentication.dto;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import java.time.LocalDate;
import java.util.Arrays;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@DisplayName("Test exception response DTO")
public class ExceptionResponseTest {
    private static final ObjectMapper MAPPER = new ObjectMapper();

    private static final long NOW = LocalDate.now().toEpochDay();

    private ExceptionResponse response;

    private long timeStamp;
    private String errorMessage;
    private int errorCode;
    private String errorDetails;

    @BeforeEach
    void setUp() {
        timeStamp = NOW;
        errorMessage = RandomStringUtils.randomAlphanumeric(30);
        errorCode = Arrays.stream(HttpStatus.values()).toList().get((int) (Math.random() * HttpStatus.values().length)).value();
        errorDetails = RandomStringUtils.randomAlphanumeric(50);

        response = new ExceptionResponse(timeStamp, errorMessage, errorCode, errorDetails);
    }

    @AfterEach
    void tearDown() {
        response = null;
    }

    @Test
    public void testRecordFields() {
        assertThat(response).isNotNull();
        assertThat(response.getTimeStamp()).isEqualTo(timeStamp);
        assertThat(response.getErrorMessage()).isEqualTo(errorMessage);
        assertThat(response.getErrorCode()).isEqualTo(errorCode);
        assertThat(response.getErrorDetails()).isEqualTo(errorDetails);
    }

    @Test
    public void testEqualsAndHashCode() {
        ExceptionResponse requestEquals = new ExceptionResponse(timeStamp, errorMessage, errorCode, errorDetails);

        assertThat(response).isEqualTo(requestEquals);
        assertThat(response.hashCode()).isEqualTo(requestEquals.hashCode());
    }

    @Test
    public void testNotEqualsAndHashCode() {
        ExceptionResponse requestNotEquals = new ExceptionResponse(
                LocalDate.now().toEpochDay(),
                RandomStringUtils.randomAlphanumeric(30),
                1,
                RandomStringUtils.randomAlphanumeric(50)
        );

        assertThat(response).isNotEqualTo(requestNotEquals);
        assertThat(response.hashCode()).isNotEqualTo(requestNotEquals.hashCode());
    }

    @Test
    public void testSerialize() throws Exception {
        String json = MAPPER.writeValueAsString(response);
        ExceptionResponse actualRequest = MAPPER.readValue(json, ExceptionResponse.class);

        String expectedJson = "{" +
                "\"time_stamp\":" + response.getTimeStamp() + "," +
                "\"error_message\":\"" + response.getErrorMessage() + "\"," +
                "\"error_code\":\"" + response.getErrorCode() + "\"," +
                "\"error_details\":\"" + response.getErrorDetails() + "\"" +
                "}";
        ExceptionResponse expectedRequest = MAPPER.readValue(expectedJson, ExceptionResponse.class);

        assertThat(expectedRequest).isEqualTo(actualRequest);
    }

    @Test
    public void testDeserialize() throws Exception {
        String json = "{" +
                "\"time_stamp\":" + response.getTimeStamp() + "," +
                "\"error_message\":\"" + response.getErrorMessage() + "\"," +
                "\"error_code\":\"" + response.getErrorCode() + "\"," +
                "\"error_details\":\"" + response.getErrorDetails() + "\"" +
                "}";

        ExceptionResponse requestMapped = MAPPER.readValue(json, ExceptionResponse.class);

        assertThat(requestMapped).usingRecursiveComparison().isEqualTo(response);
    }

    @Test
    public void testFromErrorMessage() {
        ExceptionResponse response = ExceptionResponse.fromErrorMessage(errorMessage,errorCode, errorDetails);

        assertThat(response).isNotNull();
        assertThat(response.getErrorMessage()).isEqualTo(errorMessage);
        assertThat(response.getErrorCode()).isEqualTo(errorCode);
        assertThat(response.getErrorDetails()).isEqualTo(errorDetails);
    }

    @Test
    public void testToString() {
        final ExceptionResponse response = new ExceptionResponse(timeStamp, errorMessage, errorCode, errorDetails);

        final String exceptedToString = "ExceptionResponse(timeStamp=%s, errorMessage=%s, errorCode=%s, errorDetails=%s)".formatted(timeStamp, errorMessage, errorCode, errorDetails);
        final String exceptedToStringLombok = "ExceptionResponse.ExceptionResponseBuilder(timeStamp=%s, errorMessage=%s, errorCode=%s, errorDetails=%s)".formatted(timeStamp, errorMessage, errorCode, errorDetails);

        assertThat(response).isNotNull();
        assertThat(response.toString()).isEqualTo(exceptedToString);
        assertThat(ExceptionResponse.builder()
                .timeStamp(timeStamp)
                .errorMessage(errorMessage)
                .errorCode(errorCode)
                .errorDetails(errorDetails)
                .toString()
        ).isEqualTo(exceptedToStringLombok);
    }
}