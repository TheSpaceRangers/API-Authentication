package fr.bio.apiauthentication.dto.authentication;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;

public record LoginRequest(
        @NotNull @NotNull @Email String email,
        @NotNull @NotNull String password
) {
}