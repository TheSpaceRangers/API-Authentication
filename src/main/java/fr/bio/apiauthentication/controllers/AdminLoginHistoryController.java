package fr.bio.apiauthentication.controllers;

import fr.bio.apiauthentication.dto.admin.LoginHistoryRequest;
import fr.bio.apiauthentication.dto.admin.LoginHistoryStructureResponse;
import fr.bio.apiauthentication.services.interfaces.IAdminLoginHistoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/admin")
public class AdminLoginHistoryController {
    private final IAdminLoginHistoryService loginHistoryService;

    @GetMapping(value = "/login-histories")
    public ResponseEntity<List<LoginHistoryStructureResponse>> getAllLoginHistory(
            @RequestHeader("Authorization") String token
    ) {
        return loginHistoryService.getAllLoginHistory(token);
    }

    @GetMapping(value = "/login-histories/user")
    public ResponseEntity<List<LoginHistoryStructureResponse>> getAllLoginHistoryByUser(
            @RequestHeader("Authorization") String token,
            @RequestBody LoginHistoryRequest request
    ) {
        return loginHistoryService.getAllLoginHistoryByUser(token, request);
    }
}