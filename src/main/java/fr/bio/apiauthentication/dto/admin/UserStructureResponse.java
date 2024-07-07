package fr.bio.apiauthentication.dto.admin;

import com.fasterxml.jackson.annotation.JsonProperty;
import fr.bio.apiauthentication.entities.User;
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
public class UserStructureResponse {
    @JsonProperty("id_user")
    private long idUser;

    @JsonProperty("email")
    private String email;


    @JsonProperty("first_name")
    private String firstName;

    @JsonProperty("last_name")
    private String lastName;

    @JsonProperty("created_at")
    private long createdAt;

    @JsonProperty("created_by")
    private String createdBy;

    @JsonProperty("modified_at")
    private long modifiedAt;

    @JsonProperty("modified_by")
    private String modifiedBy;

    @JsonProperty("enabled")
    private boolean enabled = true;

    @JsonProperty("roles")
    private Collection<String> roles;

    public static UserStructureResponse fromUser(User user) {
        return UserStructureResponse.builder()
                .idUser(user.getIdUser())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .createdAt(user.getCreatedAt().toEpochDay())
                .createdBy(user.getCreatedBy())
                .modifiedAt(user.getModifiedAt().toEpochDay())
                .modifiedBy(user.getModifiedBy())
                .enabled(user.isEnabled())
                .roles(user.getRoles() != null
                    ? user.getRoles().stream()
                        .map(role -> role.getAuthority() + " : " + role.getDisplayName())
                        .toList()
                    : List.of()
                ).build();
    }

    public static List<UserStructureResponse> fromUsers(List<User> users) {
        return users.stream()
                .map(UserStructureResponse::fromUser)
                .toList();
    }
}
