package fr.bio.apiauthentication.dto.account;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record UpdateUserProfilRequest(
        @NotNull @NotNull String token,
        String firstName,
        String lastName,
        @Email String email
) {
}
