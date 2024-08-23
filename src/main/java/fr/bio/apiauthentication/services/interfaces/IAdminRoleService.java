package fr.bio.apiauthentication.services.interfaces;

import fr.bio.apiauthentication.dto.MessageResponse;
import fr.bio.apiauthentication.dto.admin.RoleRequest;
import fr.bio.apiauthentication.dto.admin.RoleStructureResponse;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface IAdminRoleService {
    ResponseEntity<List<RoleStructureResponse>> getAllByStatus(String token, Boolean isActive);

    ResponseEntity<MessageResponse> newRole(String token, RoleRequest request);

    ResponseEntity<MessageResponse> modify(String token, RoleRequest request);

    ResponseEntity<MessageResponse> modifyStatus(String token, RoleRequest request, boolean status);
}