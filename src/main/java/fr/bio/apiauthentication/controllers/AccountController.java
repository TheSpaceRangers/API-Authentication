package fr.bio.apiauthentication.controllers;

import fr.bio.apiauthentication.dto.MessageResponse;
import fr.bio.apiauthentication.dto.account.UpdatePasswordRequest;
import fr.bio.apiauthentication.dto.account.UpdateUserProfilRequest;
import fr.bio.apiauthentication.dto.account.UserProfilResponse;
import fr.bio.apiauthentication.services.interfaces.IAccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/account")
public class AccountController {
    private final IAccountService accountService;

    @PostMapping(value = "/profile")
    public ResponseEntity<UserProfilResponse> login(
            @RequestHeader("Authorization") String token
    ) {
        return accountService.getUserProfile(token.substring(7));
    }

    @PutMapping(value = "/profile")
    public ResponseEntity<MessageResponse> updateProfile(
            @RequestHeader("Authorization") String token,
            @Validated @RequestBody UpdateUserProfilRequest request
    ) {
        return accountService.updateProfile(token.substring(7), request);
    }

    @PutMapping(value = "/password")
    public ResponseEntity<MessageResponse> updatePassword(
            @RequestHeader("Authorization") String token,
            @Validated @RequestBody UpdatePasswordRequest request
    ) {
        return accountService.updatePassword(token.substring(7), request);
    }

    @PutMapping(value = "/deactivate")
    public ResponseEntity<MessageResponse> desactivate(
            @RequestHeader("Authorization") String token
    ) {
        return accountService.deactivateAccount(token.substring(7));
    }
}