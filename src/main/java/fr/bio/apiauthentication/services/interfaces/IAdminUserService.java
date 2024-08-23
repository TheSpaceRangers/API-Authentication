package fr.bio.apiauthentication.services.interfaces;

import fr.bio.apiauthentication.dto.MessageResponse;
import fr.bio.apiauthentication.dto.admin.UserRequest;
import fr.bio.apiauthentication.dto.admin.UserStructureResponse;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface IAdminUserService {
    ResponseEntity<List<UserStructureResponse>> getAllUsers(String token);

    ResponseEntity<MessageResponse> newUser(String token, UserRequest request);

    ResponseEntity<MessageResponse> modify(String token, UserRequest request);

    ResponseEntity<MessageResponse> modifyStatus(String token, UserRequest request, boolean status);
}