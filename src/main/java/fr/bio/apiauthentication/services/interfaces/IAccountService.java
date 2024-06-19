package fr.bio.apiauthentication.services.interfaces;

import fr.bio.apiauthentication.dto.account.UpdatePasswordRequest;
import fr.bio.apiauthentication.dto.account.UpdateUserProfilRequest;
import fr.bio.apiauthentication.dto.account.UserProfilRequest;
import fr.bio.apiauthentication.dto.account.UserProfilResponse;
import org.springframework.http.ResponseEntity;

public interface IAccountService {
    ResponseEntity<UserProfilResponse> getUserProfile(UserProfilRequest request);

    ResponseEntity<UserProfilResponse> updateUserProfile(UpdateUserProfilRequest request);

    ResponseEntity<UserProfilResponse> updatePassword(UpdatePasswordRequest request);
}