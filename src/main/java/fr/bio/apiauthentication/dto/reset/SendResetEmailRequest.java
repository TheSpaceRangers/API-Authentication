package fr.bio.apiauthentication.dto.reset;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record SendResetEmailRequest(
        @JsonProperty("email") @NotNull @NotBlank @Email String email
) {
}