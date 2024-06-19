package fr.bio.apiauthentication.dto.account;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class UserProfilResponse {
    @JsonProperty("first_name")
    private String firstName;

    @JsonProperty("last_name")
    private String lastName;

    @JsonProperty("email")
    private String email;

    @JsonProperty("roles")
    private List<String> roles;
}
