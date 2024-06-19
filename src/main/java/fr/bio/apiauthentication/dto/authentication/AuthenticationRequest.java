package fr.bio.apiauthentication.dto.authentication;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;

public record AuthenticationRequest (
        @NotNull @NotNull @Email String email,
        @NotNull @NotNull String password
) {
}
