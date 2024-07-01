package fr.bio.apiauthentication.services.interfaces;

import fr.bio.apiauthentication.dto.MessageResponse;
import fr.bio.apiauthentication.dto.admin.RoleRequest;
import fr.bio.apiauthentication.dto.admin.RoleStructureResponse;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface IAdminRoleService {
    ResponseEntity<List<RoleStructureResponse>> getAllRolesByStatus(String token, Boolean isActive);

    ResponseEntity<MessageResponse> newRole(String token, RoleRequest request);

    ResponseEntity<MessageResponse> modifyRole(String token, RoleRequest request);

    ResponseEntity<MessageResponse> updateRoleStatus(String token, RoleRequest request, boolean status);
}