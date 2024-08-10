package fr.bio.apiauthentication.dto.admin;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record UserRolesRequest(
        @NotNull @NotBlank @Email String email,
        List<String> rolesToAdd,
        List<String> rolesToRemove
) {
}