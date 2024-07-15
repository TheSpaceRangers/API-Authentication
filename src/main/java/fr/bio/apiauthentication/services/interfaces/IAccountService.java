package fr.bio.apiauthentication.services.interfaces;

import fr.bio.apiauthentication.dto.MessageResponse;
import fr.bio.apiauthentication.dto.account.UpdateUserProfilRequest;
import fr.bio.apiauthentication.dto.admin.UserStructureResponse;
import org.springframework.http.ResponseEntity;

public interface IAccountService {
    ResponseEntity<UserStructureResponse> getUserStructure(String token);

    ResponseEntity<MessageResponse> modify(String token, UpdateUserProfilRequest request);

    ResponseEntity<MessageResponse> modifyStatus(String token, boolean status);
}