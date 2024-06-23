package fr.bio.apiauthentication.services.interfaces;

import fr.bio.apiauthentication.dto.MessageResponse;
import fr.bio.apiauthentication.dto.account.UpdatePasswordRequest;
import fr.bio.apiauthentication.dto.account.UpdateUserProfilRequest;
import fr.bio.apiauthentication.dto.account.UserProfilResponse;
import org.springframework.http.ResponseEntity;

public interface IAccountRoleService {
    ResponseEntity<UserProfilResponse> getUserProfile(String token);

    ResponseEntity<MessageResponse> updateProfile(String token, UpdateUserProfilRequest request);

    ResponseEntity<MessageResponse> updatePassword(String token, UpdatePasswordRequest request);

    ResponseEntity<MessageResponse> deactivateAccount(String token);
}