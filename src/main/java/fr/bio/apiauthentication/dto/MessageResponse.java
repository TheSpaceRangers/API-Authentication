package fr.bio.apiauthentication.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MessageResponse {
    @JsonProperty("message")
    private String message;

    public static MessageResponse fromMessage(String message) {
        return MessageResponse.builder()
                .message(message)
                .build();
    }
}