package fr.bio.apiauthentication.services.interfaces;

import fr.bio.apiauthentication.dto.MessageResponse;
import fr.bio.apiauthentication.dto.admin.UserModificationRequest;
import fr.bio.apiauthentication.dto.admin.UserStructureResponse;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface IAdminUserService {
    ResponseEntity<List<UserStructureResponse>> getAllUsers(String token);

    ResponseEntity<MessageResponse> createUser(String token, UserModificationRequest request);

    ResponseEntity<MessageResponse> updateUser(String token, UserModificationRequest request);

    ResponseEntity<MessageResponse> updateUserStatus(String token, UserModificationRequest request, boolean status);
}
