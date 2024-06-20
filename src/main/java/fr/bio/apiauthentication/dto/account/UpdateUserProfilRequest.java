package fr.bio.apiauthentication.dto.account;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;

public record UpdateUserProfilRequest(
        @NotNull @NotNull String token,
        @JsonProperty("first_name") String firstName,
        @JsonProperty("last_name") String lastName,
        @Email String email
) {
}