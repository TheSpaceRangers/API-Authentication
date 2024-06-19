package fr.bio.apiauthentication.dto.account;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record UpdatePasswordRequest (
        @NotNull @NotBlank String token,
        @NotNull @NotBlank String oldPassword,
        @NotNull @NotBlank String newPassword
) {
}
