package fr.bio.apiauthentication.services.interfaces;

import fr.bio.apiauthentication.dto.MessageResponse;
import fr.bio.apiauthentication.dto.admin.UpdateRoleRequest;
import org.springframework.http.ResponseEntity;

public interface IAdminService {
    ResponseEntity<MessageResponse> updateRole(String token, UpdateRoleRequest request);
}
