package fr.bio.apiauthentication.controllers;

import fr.bio.apiauthentication.dto.MessageResponse;
import fr.bio.apiauthentication.dto.reset.ResetPasswordRequest;
import fr.bio.apiauthentication.dto.reset.SendResetEmailRequest;
import fr.bio.apiauthentication.services.interfaces.IResetService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api-auth/v1/reset")
public class ResetController {
    private final IResetService resetService;

    @PostMapping(value = "/password")
    public ResponseEntity<MessageResponse> passwordResetEmail(
            @RequestHeader("Authorization") String token,
            @Validated  @RequestBody SendResetEmailRequest request
    ) {
        return resetService.passwordResetEmail(token, request);
    }

    @PutMapping(value = "/password")
    public ResponseEntity<MessageResponse> resetPassword(
            @RequestParam(value = "token") String token,
            @Validated @RequestBody ResetPasswordRequest request
    ) {
        return resetService.resetPassword(token, request);
    }
}