package fr.bio.apiauthentication.services.interfaces;

import fr.bio.apiauthentication.dto.account.UserProfilRequest;
import fr.bio.apiauthentication.dto.account.UserProfilResponse;
import org.springframework.http.ResponseEntity;

public interface IAccountService {
    ResponseEntity<UserProfilResponse> getUserProfile(UserProfilRequest request);
}
