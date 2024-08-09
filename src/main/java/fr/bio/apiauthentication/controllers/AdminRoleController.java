package fr.bio.apiauthentication.controllers;

import fr.bio.apiauthentication.dto.MessageResponse;
import fr.bio.apiauthentication.dto.admin.RoleRequest;
import fr.bio.apiauthentication.dto.admin.RoleStructureResponse;
import fr.bio.apiauthentication.enums.Messages;
import fr.bio.apiauthentication.services.interfaces.IAdminRoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api-auth/v1/admin")
public class AdminRoleController {
    private final IAdminRoleService adminRoleService;

    @GetMapping(value = "/roles")
    public ResponseEntity<List<RoleStructureResponse>> getAllByStatus(
            @RequestHeader("Authorization") String token,
            @RequestParam(value = "status", required = false) String status
    ) {
        Boolean isActive = null;

        if (status.equalsIgnoreCase("active"))
            isActive = true;
        else if (status.equalsIgnoreCase("inactive"))
            isActive = false;

        return adminRoleService.getAllByStatus(token, isActive);
    }

    @PostMapping(value = "/role/new")
    public ResponseEntity<MessageResponse> newRole(
            @RequestHeader("Authorization") String token,
            @Validated @RequestBody RoleRequest request
    ) {
        return adminRoleService.newRole(token, request);
    }

    @PutMapping(value = "/role/update")
    public ResponseEntity<MessageResponse> modify(
            @RequestHeader("Authorization") String token,
            @Validated @RequestBody RoleRequest request
    ) {
        return adminRoleService.modify(token, request);
    }

    @PutMapping(value = "/role/status")
    public ResponseEntity<MessageResponse> modifyStatus(
            @RequestHeader("Authorization") String token,
            @RequestParam(value = "action") String action,
            @Validated @RequestBody RoleRequest request
    ) {
        boolean status;

        if (action.equalsIgnoreCase("activate"))
            status = true;
        else if (action.equalsIgnoreCase("deactivate"))
            status = false;
        else
            throw new IllegalArgumentException(Messages.STATUS_PARAMETER_INVALID.formatMessage());

        return adminRoleService.modifyStatus(token, request, status);
    }
}