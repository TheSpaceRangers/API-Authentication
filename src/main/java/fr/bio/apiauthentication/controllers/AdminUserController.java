package fr.bio.apiauthentication.controllers;

import fr.bio.apiauthentication.dto.MessageResponse;
import fr.bio.apiauthentication.dto.admin.UserRequest;
import fr.bio.apiauthentication.dto.admin.UserStructureResponse;
import fr.bio.apiauthentication.enums.Messages;
import fr.bio.apiauthentication.services.interfaces.IAdminUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/admin")
public class AdminUserController {
    private final IAdminUserService adminUserService;

    @GetMapping(value = "/users")
    public ResponseEntity<List<UserStructureResponse>> getAllUsers (
            @RequestHeader("Authorization") String token
    ) {
        return adminUserService.getAllUsers(token);
    }

    @PostMapping(value = "/user/new")
    public ResponseEntity<MessageResponse> createUser (
            @RequestHeader("Authorization") String token,
            @Validated @RequestBody UserRequest request
    ) {
        return adminUserService.createUser(token, request);
    }

    @PutMapping(value = "/user/update")
    public ResponseEntity<MessageResponse> updateUser (
            @RequestHeader("Authorization") String token,
            @Validated @RequestBody UserRequest request
    ) {
        return adminUserService.updateUser(token, request);
    }

    @PutMapping(value = "/user/status")
    public ResponseEntity<MessageResponse> updateUserStatus (
            @RequestHeader("Authorization") String token,
            @RequestParam(value = "action") String action,
            @Validated @RequestBody UserRequest request
    ) {
        boolean status;

        if (action.equalsIgnoreCase("activate"))
            status = true;
        else if (action.equalsIgnoreCase("deactivate"))
            status = false;
        else
            throw new IllegalArgumentException(Messages.STATUS_PARAMETER_INVALID.formatMessage());

        return adminUserService.updateUserStatus(token, request, status);
    }
}
