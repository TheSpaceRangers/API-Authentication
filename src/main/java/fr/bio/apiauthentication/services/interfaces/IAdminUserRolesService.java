package fr.bio.apiauthentication.services.interfaces;

import fr.bio.apiauthentication.dto.MessageResponse;
import fr.bio.apiauthentication.dto.admin.UserRolesRequest;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface IAdminUserRolesService {
    ResponseEntity<MessageResponse> modifyUserRoles(String token, UserRolesRequest request);

    ResponseEntity<List<ResponseEntity<MessageResponse>>> modifyUsersRoles(String token, List<UserRolesRequest> requests);
}