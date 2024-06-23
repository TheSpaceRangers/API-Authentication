package fr.bio.apiauthentication.controllers;

import fr.bio.apiauthentication.dto.MessageResponse;
import fr.bio.apiauthentication.dto.admin.UpdateRoleRequest;
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
            @Valid @RequestBody UpdateRoleRequest request
    ) {
        return adminService.updateRole(token, request);
    }
}
