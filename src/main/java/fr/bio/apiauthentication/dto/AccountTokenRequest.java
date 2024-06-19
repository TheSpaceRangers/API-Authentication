package fr.bio.apiauthentication.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record AccountTokenRequest(
        @NotNull @NotBlank String token
) {
}
