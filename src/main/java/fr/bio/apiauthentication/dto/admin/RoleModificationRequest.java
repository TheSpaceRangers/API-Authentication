package fr.bio.apiauthentication.dto.admin;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record RoleModificationRequest(
        @NotNull @NotBlank String authority,
        @JsonProperty("display_name") String displayName,
        @JsonProperty("description") String description
) {
}
