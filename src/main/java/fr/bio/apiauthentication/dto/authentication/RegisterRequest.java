package fr.bio.apiauthentication.dto.authentication;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record RegisterRequest(
        @NotNull @NotBlank @JsonProperty("first_name") String firstName,
        @NotNull @NotBlank @JsonProperty("last_name") String lastName,
        @NotNull @NotBlank @JsonProperty("email") @Email String email,
        @NotNull @NotBlank @JsonProperty("password") String password
) {
}