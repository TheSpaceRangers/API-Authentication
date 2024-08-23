package fr.bio.apiauthentication.controllers;

import fr.bio.apiauthentication.dto.MessageResponse;
import fr.bio.apiauthentication.dto.account.UpdateUserProfilRequest;
import fr.bio.apiauthentication.dto.admin.UserStructureResponse;
import fr.bio.apiauthentication.enums.Messages;
import fr.bio.apiauthentication.services.interfaces.IAccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api-auth/v1/account")
public class AccountController {
    private final IAccountService accountService;

    @PostMapping(value = "/profile")
    public ResponseEntity<UserStructureResponse> userStructure(
            @RequestHeader("Authorization") String token
    ) {
        return accountService.getUserStructure(token.substring(7));
    }

    @PutMapping(value = "/profile")
    public ResponseEntity<MessageResponse> modify(
            @RequestHeader("Authorization") String token,
            @Validated @RequestBody UpdateUserProfilRequest request
    ) {
        return accountService.modify(token.substring(7), request);
    }

    @PutMapping(value = "/status")
    public ResponseEntity<MessageResponse> modifyStatus(
            @RequestHeader("Authorization") String token,
            @RequestParam(value = "action") String action
    ) {
        boolean status;

        if (action.equalsIgnoreCase("activate"))
            status = true;
        else if (action.equalsIgnoreCase("deactivate"))
            status = false;
        else
            throw new IllegalArgumentException(Messages.STATUS_PARAMETER_INVALID.formatMessage());

        return accountService.modifyStatus(token.substring(7), status);
    }
}