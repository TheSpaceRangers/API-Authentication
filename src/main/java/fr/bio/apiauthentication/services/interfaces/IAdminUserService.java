package fr.bio.apiauthentication.services.interfaces;

import fr.bio.apiauthentication.dto.MessageResponse;
import fr.bio.apiauthentication.dto.admin.UserRequest;
import fr.bio.apiauthentication.dto.admin.UserStructureResponse;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface IAdminUserService {
    ResponseEntity<List<UserStructureResponse>> getAllUsers(String token);

    ResponseEntity<MessageResponse> createUser(String token, UserRequest request);

    ResponseEntity<MessageResponse> updateUser(String token, UserRequest request);

    ResponseEntity<MessageResponse> updateUserStatus(String token, UserRequest request, boolean status);
}
