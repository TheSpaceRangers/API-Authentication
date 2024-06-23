package fr.bio.apiauthentication.controllers;

import fr.bio.apiauthentication.dto.MessageResponse;
import fr.bio.apiauthentication.dto.admin.UserModificationRequest;
import fr.bio.apiauthentication.dto.admin.UserStructureResponse;
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
            @Validated @RequestBody UserModificationRequest request
    ) {
        return adminUserService.createUser(token, request);
    }

    @PutMapping(value = "/user/update")
    public ResponseEntity<MessageResponse> updateUser (
            @RequestHeader("Authorization") String token,
            @Validated @RequestBody UserModificationRequest request
    ) {
        return adminUserService.updateUser(token, request);
    }
}
