package fr.bio.apiauthentication.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ExceptionResponse {
    @JsonProperty("time_stamp")
    private Long timeStamp;

    @JsonProperty("error_message")
    private String errorMessage;

    @JsonProperty("error_code")
    private int errorCode;

    @JsonProperty("error_details")
    private String errorDetails;

    public static ExceptionResponse fromErrorMessage(
            String errorMessage,
            int errorCode,
            String errorDetails
    ) {
        return ExceptionResponse.builder()
                .timeStamp(LocalDate.now().toEpochDay())
                .errorMessage(errorMessage)
                .errorCode(errorCode)
                .errorDetails(errorDetails)
                .build();
    }
}