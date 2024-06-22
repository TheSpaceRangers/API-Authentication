package fr.bio.apiauthentication.services.interfaces;

import fr.bio.apiauthentication.dto.MessageResponse;
import fr.bio.apiauthentication.dto.account.UpdatePasswordRequest;
import fr.bio.apiauthentication.dto.account.UpdateUserProfilRequest;
import fr.bio.apiauthentication.dto.AccountTokenRequest;
import fr.bio.apiauthentication.dto.account.UserProfilResponse;
import org.springframework.http.ResponseEntity;

public interface IAccountService {
    ResponseEntity<UserProfilResponse> getUserProfile(AccountTokenRequest request);

    ResponseEntity<MessageResponse> updateProfile(UpdateUserProfilRequest request);

    ResponseEntity<MessageResponse> updatePassword(UpdatePasswordRequest request);

    ResponseEntity<MessageResponse> deactivateAccount(AccountTokenRequest request);
}