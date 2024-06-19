package fr.bio.apiauthentication.dto.account;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record UserProfilRequest(
        @NotNull @NotBlank String token
) {
}
