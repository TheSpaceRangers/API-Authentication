package fr.bio.apiauthentication.dto.admin;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Email;

public record LoginHistoryRequest (
        @JsonProperty("email") @Email String email
) {}