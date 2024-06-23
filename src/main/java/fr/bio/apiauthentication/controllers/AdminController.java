package fr.bio.apiauthentication.controllers;

import fr.bio.apiauthentication.dto.MessageResponse;
import fr.bio.apiauthentication.dto.admin.RoleModificationRequest;
import fr.bio.apiauthentication.dto.admin.RoleStructureResponse;
import fr.bio.apiauthentication.enums.Messages;
import fr.bio.apiauthentication.services.AdminService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/admin")
public class AdminController {
    private final AdminService adminService;

    @GetMapping(value = "/roles")
    public ResponseEntity<List<RoleStructureResponse>> getRoles(
            @RequestHeader("Authorization") String token,
            @RequestParam(value = "status", required = false) String status
    ) {
        return adminService.getAllRolesByStatus(
                token,
                status == null ? null : status.equalsIgnoreCase("active")
                        ? true : status.equalsIgnoreCase("inactive")
                        ? false : null
        );
    }

    @PostMapping(value = "/role/new")
    public ResponseEntity<MessageResponse> createRole(
            @RequestHeader("Authorization") String token,
            @Valid @RequestBody RoleModificationRequest request
    ) {
        return adminService.createRole(token, request);
    }

    @PutMapping(value = "/role/update")
    public ResponseEntity<MessageResponse> updateRole(
            @RequestHeader("Authorization") String token,
            @Valid @RequestBody RoleModificationRequest request
    ) {
        return adminService.updateRole(token, request);
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
            throw new IllegalArgumentException(Messages.ROLE_STATUS_PARAMETER_INVALID.formatMessage(""));

        return adminService.updateRoleStatus(token, request, status);
    }
}
