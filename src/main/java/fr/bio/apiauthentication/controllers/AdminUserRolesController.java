package fr.bio.apiauthentication.controllers;

import fr.bio.apiauthentication.dto.MessageResponse;
import fr.bio.apiauthentication.dto.admin.UserRolesRequest;
import fr.bio.apiauthentication.services.interfaces.IAdminUserRolesService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api-auth/v1/admin/users-roles")
@RequiredArgsConstructor
public class AdminUserRolesController {
    private final IAdminUserRolesService adminUserRolesService;

    @PostMapping(value = "/modify")
    public ResponseEntity<MessageResponse> modifyUserRoles(
            @RequestHeader("Authorization") String token,
            @RequestBody @Validated UserRolesRequest request
    ) {
        return adminUserRolesService.modifyUserRoles(token, request);
    }

    @PostMapping(value = "/modifies")
    public ResponseEntity<List<ResponseEntity<MessageResponse>>> modifyUsersRoles(
            @RequestHeader("Authorization") String token,
            @RequestBody @Validated List<UserRolesRequest> request
    ) {
        return adminUserRolesService.modifyUsersRoles(token, request);
    }
}