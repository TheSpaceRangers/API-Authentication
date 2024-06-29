package fr.bio.apiauthentication.dto.reset;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ResetPasswordRequest (
        @JsonProperty("password") @NotNull @NotBlank String password
) {
}
