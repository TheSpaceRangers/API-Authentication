package fr.bio.apiauthentication.controllers;

import fr.bio.apiauthentication.dto.account.UpdateUserProfilRequest;
import fr.bio.apiauthentication.dto.account.UserProfilRequest;
import fr.bio.apiauthentication.dto.account.UserProfilResponse;
import fr.bio.apiauthentication.services.AccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/account")
public class AccountController {
    private final AccountService accountService;

    @PostMapping(value = "/profil")
    public ResponseEntity<UserProfilResponse> login(
            @Validated @RequestBody UserProfilRequest request
    ) {
        return accountService.getUserProfile(request);
    }

    @PutMapping(value = "/profil")
    public ResponseEntity<UserProfilResponse> updateProfile(
            @Validated @RequestBody UserProfilRequest request,
            @Validated @RequestBody UpdateUserProfilRequest updateRequest
    ) {
        return accountService.updateUserProfile(request, updateRequest);
    }
}
