package fr.bio.apiauthentication.services;

import fr.bio.apiauthentication.components.HttpHeadersUtil;
import fr.bio.apiauthentication.dto.MessageResponse;
import fr.bio.apiauthentication.dto.admin.UserModificationRequest;
import fr.bio.apiauthentication.dto.admin.UserStructureResponse;
import fr.bio.apiauthentication.entities.Role;
import fr.bio.apiauthentication.entities.User;
import fr.bio.apiauthentication.enums.Messages;
import fr.bio.apiauthentication.exceptions.RoleNotFoundException;
import fr.bio.apiauthentication.exceptions.UserAlreadyExistsException;
import fr.bio.apiauthentication.repositories.RoleRepository;
import fr.bio.apiauthentication.repositories.UserRepository;
import fr.bio.apiauthentication.services.interfaces.IAdminUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class AdminUserService implements IAdminUserService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    private final HttpHeadersUtil httpHeadersUtil;

    @Override
    public ResponseEntity<List<UserStructureResponse>> getAllUsers(
            String token
    ) {
        List<User> users = userRepository.findAll();

        List<UserStructureResponse> userStructures = users.stream()
                .map(UserStructureResponse::fromUser)
                .toList();

        return ResponseEntity.ok()
                .headers(httpHeadersUtil.createHeaders(token))
                .body(userStructures);
    }

    @Override
    public ResponseEntity<MessageResponse> createUser(
            String token,
            UserModificationRequest request
    ) {
        User existingUser = userRepository.findByEmail(request.email())
                .orElse(null);

        if (existingUser != null)
            throw new UserAlreadyExistsException(Messages.USER_ALREADY_EXISTS.formatMessage(request.email()));

        List<Role> roles = request.roles() != null
                ? request.roles().stream()
                    .map(role -> roleRepository.findByAuthority(role)
                            .orElseThrow(() -> new RoleNotFoundException(Messages.ROLE_NOT_FOUND.formatMessage(role))))
                    .toList()
                : List.of();

        User user = User.builder()
                .email(request.email())
                .firstName(request.firstName())
                .lastName(request.lastName())
                .roles(roles)
                .build();
        userRepository.save(user);

        return ResponseEntity.ok()
                .headers(httpHeadersUtil.createHeaders(token))
                .body(new MessageResponse(Messages.USER_CREATED.formatMessage(request.email())));
    }
}