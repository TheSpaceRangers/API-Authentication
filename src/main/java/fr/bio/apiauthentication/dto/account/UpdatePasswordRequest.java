package fr.bio.apiauthentication.dto.account;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record UpdatePasswordRequest (
        @NotNull @NotBlank String token,
        @JsonProperty("old_password") @NotNull @NotBlank String oldPassword,
        @JsonProperty("new_password") @NotNull @NotBlank String newPassword
) {
}
