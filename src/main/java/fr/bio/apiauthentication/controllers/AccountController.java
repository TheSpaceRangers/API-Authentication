package fr.bio.apiauthentication.controllers;

import fr.bio.apiauthentication.dto.MessageResponse;
import fr.bio.apiauthentication.dto.account.UpdatePasswordRequest;
import fr.bio.apiauthentication.dto.account.UpdateUserProfilRequest;
import fr.bio.apiauthentication.dto.AccountTokenRequest;
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

    // TODO Front Token delete BEARER

    @PostMapping(value = "/profil")
    public ResponseEntity<UserProfilResponse> login(
            @Validated @RequestBody AccountTokenRequest request
    ) {
        return accountService.getUserProfile(request);
    }

    @PutMapping(value = "/profil")
    public ResponseEntity<MessageResponse> updateProfile(
            @Validated @RequestBody UpdateUserProfilRequest request
    ) {
        return accountService.updateProfile(request);
    }

    @PutMapping(value = "/password")
    public ResponseEntity<MessageResponse> updatePassword(
            @Validated @RequestBody UpdatePasswordRequest request
    ) {
        return accountService.updatePassword(request);
    }

    @PutMapping(value = "/desactivate")
    public ResponseEntity<MessageResponse> desactivate(
            @Validated @RequestBody AccountTokenRequest request
    ) {
        return accountService.deactivateAccount(request);
    }
}
