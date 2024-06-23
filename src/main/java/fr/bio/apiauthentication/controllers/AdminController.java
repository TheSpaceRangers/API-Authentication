package fr.bio.apiauthentication.controllers;

import fr.bio.apiauthentication.dto.MessageResponse;
import fr.bio.apiauthentication.dto.admin.RoleModificationRequest;
import fr.bio.apiauthentication.services.AdminService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/admin")
public class AdminController {
    private final AdminService adminService;

    @PutMapping(value = "/role/update")
    public ResponseEntity<MessageResponse> updateRole(
            @RequestHeader("Authorization") String token,
            @Valid @RequestBody RoleModificationRequest request
    ) {
        return adminService.updateRole(token, request);
    }

    @PutMapping(value = "/role/deactivate")
    public ResponseEntity<MessageResponse> deactivateRole(
            @RequestHeader("Authorization") String token,
            @Valid @RequestBody RoleModificationRequest request
    ) {
        return adminService.deactivateRole(token, request);
    }
}
