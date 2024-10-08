package fr.bio.apiauthentication.dto.admin;

import com.fasterxml.jackson.annotation.JsonProperty;
import fr.bio.apiauthentication.entities.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Collection;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RoleStructureResponse {
    @JsonProperty("id_role")
    private long idRole;

    @JsonProperty("authority")
    private String authority;


    @JsonProperty("display_name")
    private String displayName;


    @JsonProperty("description")
    private String description;


    @JsonProperty("modified_at")
    private Long modifiedAt;


    @JsonProperty("modified_by")
    private String modifiedBy;


    @JsonProperty("enabled")
    private boolean enabled;


    @JsonProperty("users")
    private Collection<String> users;

    public static RoleStructureResponse fromRole(Role role) {
        return role != null
            ? RoleStructureResponse.builder()
                .idRole(role.getIdRole())
                .authority(role.getAuthority())
                .displayName(role.getDisplayName())
                .description(role.getDescription())
                .modifiedAt(role.getModifiedAt().toEpochDay())
                .modifiedBy(role.getModifiedBy())
                .enabled(role.isEnabled())
                .users(role.getUsers() != null
                        ? role.getUsers().stream()
                            .map(user -> user.getFirstName() + " " + user.getLastName())
                            .toList()
                        : List.of()
                ).build()
            : null;
    }

    public static List<RoleStructureResponse> fromRoles(List<Role> roles) {
        return roles != null
                ? roles.stream()
                    .map(RoleStructureResponse::fromRole)
                    .toList()
                : List.of();
    }
}