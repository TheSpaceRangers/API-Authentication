package fr.bio.apiauthentication.controllers;

import fr.bio.apiauthentication.dto.MessageResponse;
import fr.bio.apiauthentication.dto.admin.RoleModificationRequest;
import fr.bio.apiauthentication.dto.admin.RoleStructureResponse;
import fr.bio.apiauthentication.enums.Messages;
import fr.bio.apiauthentication.services.interfaces.IAdminRoleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/admin")
public class AdminRoleController {
    private final IAdminRoleService adminRoleService;

    @GetMapping(value = "/roles")
    public ResponseEntity<List<RoleStructureResponse>> getRoles(
            @RequestHeader("Authorization") String token,
            @RequestParam(value = "status", required = false) String status
    ) {
        Boolean isActive = null;

        if (status.equalsIgnoreCase("active"))
            isActive = true;
        else if (status.equalsIgnoreCase("inactive"))
            isActive = false;

        return adminRoleService.getAllRolesByStatus(token, isActive);
    }

    @PostMapping(value = "/role/new")
    public ResponseEntity<MessageResponse> createRole(
            @RequestHeader("Authorization") String token,
            @Valid @RequestBody RoleModificationRequest request
    ) {
        return adminRoleService.createRole(token, request);
    }

    @PutMapping(value = "/role/update")
    public ResponseEntity<MessageResponse> updateRole(
            @RequestHeader("Authorization") String token,
            @Valid @RequestBody RoleModificationRequest request
    ) {
        return adminRoleService.updateRole(token, request);
    }

    @PutMapping(value = "/role/status")
    public ResponseEntity<MessageResponse> deactivateRole(
            @RequestHeader("Authorization") String token,
            @RequestParam(value = "action", required = true) String action,
            @Valid @RequestBody RoleModificationRequest request
    ) {
        boolean status;

        if (action.equalsIgnoreCase("activate"))
            status = true;
        else if (action.equalsIgnoreCase("deactivate"))
            status = false;
        else
            throw new IllegalArgumentException(Messages.STATUS_PARAMETER_INVALID.formatMessage(""));

        return adminRoleService.updateRoleStatus(token, request, status);
    }
}
