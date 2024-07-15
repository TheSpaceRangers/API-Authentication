package fr.bio.apiauthentication.services;

import fr.bio.apiauthentication.components.HttpHeadersUtil;
import fr.bio.apiauthentication.dto.MessageResponse;
import fr.bio.apiauthentication.dto.admin.UserRequest;
import fr.bio.apiauthentication.dto.admin.UserStructureResponse;
import fr.bio.apiauthentication.entities.Role;
import fr.bio.apiauthentication.entities.User;
import fr.bio.apiauthentication.enums.Messages;
import fr.bio.apiauthentication.exceptions.not_found.RoleNotFoundException;
import fr.bio.apiauthentication.exceptions.already_exists.UserAlreadyExistsException;
import fr.bio.apiauthentication.repositories.RoleRepository;
import fr.bio.apiauthentication.repositories.UserRepository;
import fr.bio.apiauthentication.services.interfaces.IAdminUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class AdminUserService implements IAdminUserService {
    private static final String USER = "User";
    private static final String ROLE = "Role";

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    private final HttpHeadersUtil httpHeadersUtil;

    @Override
    public ResponseEntity<List<UserStructureResponse>> getAllUsers(
            String token
    ) {
        return ResponseEntity.ok()
                .headers(httpHeadersUtil.createHeaders(token))
                .body(UserStructureResponse.fromUsers(userRepository.findAll()));
    }

    @Override
    public ResponseEntity<MessageResponse> newUser(
            String token,
            UserRequest request
    ) {
        User user = userRepository.findByEmail(request.email())
                .orElse(null);

        if (user != null)
            throw new UserAlreadyExistsException(Messages.ENTITY_ALREADY_EXISTS.formatMessage(USER, request.email()));

        List<Role> roles = request.roles() != null
                ? request.roles().stream()
                    .map(role -> roleRepository.findByAuthority(role)
                            .orElseThrow(() -> new RoleNotFoundException(Messages.ENTITY_NOT_FOUND.formatMessage(ROLE, role))))
                    .toList()
                : List.of();

        user = User.builder()
                .email(request.email())
                .firstName(request.firstName())
                .lastName(request.lastName())
                .roles(roles)
                .build();
        userRepository.save(user);

        return ResponseEntity.ok()
                .headers(httpHeadersUtil.createHeaders(token))
                .body(MessageResponse.fromMessage(Messages.ENTITY_CREATED.formatMessage(USER, request.email())));
    }

    @Override
    public ResponseEntity<MessageResponse> modify(
            String token,
            UserRequest request
    ) {
        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new UsernameNotFoundException(Messages.ENTITY_NOT_FOUND.formatMessage(USER, request.email())));

        boolean isModified = false;

        isModified |= Optional.ofNullable(request.firstName())
                .filter(firstname -> !firstname.isBlank() && !firstname.equals(user.getFirstName()))
                .map(firstname -> {
                    user.setFirstName(firstname);
                    return true;
                })
                .orElse(false);

        isModified |= Optional.ofNullable(request.lastName())
                .filter(lastname -> !lastname.isBlank() && !lastname.equals(user.getLastName()))
                .map(lastname -> {
                    user.setLastName(lastname);
                    return true;
                })
                .orElse(false);

        if (isModified)
            userRepository.save(user);

        return ResponseEntity.ok()
                .headers(httpHeadersUtil.createHeaders(token))
                .body(isModified
                        ? MessageResponse.fromMessage(Messages.ENTITY_UPDATED.formatMessage(USER, request.email()))
                        : MessageResponse.fromMessage(Messages.ENTITY_NO_MODIFIED.formatMessage(USER, request.email()))
                );
    }

    @Override
    public ResponseEntity<MessageResponse> modifyStatus(
            String token,
            UserRequest request,
            boolean status
    ) {
        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new UsernameNotFoundException(Messages.ENTITY_NOT_FOUND.formatMessage(USER, request.email())));

        user.setEnabled(status);
        userRepository.save(user);

        return ResponseEntity.ok()
                .headers(httpHeadersUtil.createHeaders(token))
                .body(status
                        ? MessageResponse.fromMessage(Messages.ENTITY_ACTIVATED.formatMessage(USER, request.email()))
                        : MessageResponse.fromMessage(Messages.ENTITY_DEACTIVATED.formatMessage(USER, request.email()))
                );
    }
}