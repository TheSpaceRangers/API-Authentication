package fr.bio.apiauthentication.services;

import fr.bio.apiauthentication.components.HttpHeadersUtil;
import fr.bio.apiauthentication.dto.MessageResponse;
import fr.bio.apiauthentication.dto.account.UpdateUserProfilRequest;
import fr.bio.apiauthentication.dto.admin.UserStructureResponse;
import fr.bio.apiauthentication.entities.User;
import fr.bio.apiauthentication.enums.Messages;
import fr.bio.apiauthentication.repositories.UserRepository;
import fr.bio.apiauthentication.services.interfaces.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class AccountService implements IAccountService {
    private static final String USER = "User";

    private final UserRepository userRepository;

    private final IJwtService jwtService;

    private final HttpHeadersUtil httpHeadersUtil;

    @Override
    public ResponseEntity<UserStructureResponse> getUserStructure(
            String token
    ) {
        final String email = jwtService.getUsernameFromToken(token);

        final User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException(Messages.ENTITY_NOT_FOUND.formatMessage(USER, email)));

        return ResponseEntity.ok()
                .headers(httpHeadersUtil.createHeaders(token))
                .body(UserStructureResponse.fromUser(user));
    }

    @Override
    public ResponseEntity<MessageResponse> modify(
            String token,
            UpdateUserProfilRequest request
    ) {
        final String email = jwtService.getUsernameFromToken(token);

        final User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException(Messages.ENTITY_NOT_FOUND.formatMessage(USER, email)));

        boolean isModified = false;

        isModified |= Optional.ofNullable(request.firstName())
                .filter(firstName -> !firstName.isBlank() && !firstName.equals(user.getFirstName()))
                .map(firstName -> {user.setFirstName(firstName); return true;})
                .orElse(false);

        isModified |= Optional.ofNullable(request.lastName())
                .filter(lastName -> !lastName.isEmpty() && !lastName.equals(user.getLastName()))
                .map(lastName -> {user.setLastName(lastName); return true;})
                .orElse(false);

        isModified |= Optional.ofNullable(request.email())
                .filter(newEmail -> !newEmail.isEmpty() && !newEmail.equals(user.getEmail()))
                .map(newEmail -> {user.setEmail(newEmail); return true;})
                .orElse(false);

        if (isModified) {
            userRepository.save(user);
        }

        return ResponseEntity.ok()
                .headers(httpHeadersUtil.createHeaders(token))
                .body(isModified
                        ? MessageResponse.fromMessage(Messages.ACCOUNT_UPDATED.formatMessage(email))
                        : MessageResponse.fromMessage(Messages.ENTITY_NO_MODIFIED.formatMessage(USER, email))
                );
    }

    @Override
    public ResponseEntity<MessageResponse> modifyStatus(
            String token,
            boolean status
    ) {
        final String email = jwtService.getUsernameFromToken(token);

        final User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException(Messages.ENTITY_NOT_FOUND.formatMessage(USER, email)));

        user.setEnabled(status);
        userRepository.save(user);

        return ResponseEntity.ok()
                .headers(httpHeadersUtil.createHeaders(token))
                .body(status
                        ? MessageResponse.fromMessage(Messages.ENTITY_ACTIVATED.formatMessage(USER, email))
                        : MessageResponse.fromMessage(Messages.ENTITY_DEACTIVATED.formatMessage(USER, email))
                );
    }
}