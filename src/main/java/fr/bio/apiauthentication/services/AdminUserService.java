package fr.bio.apiauthentication.services;

import fr.bio.apiauthentication.components.HttpHeadersUtil;
import fr.bio.apiauthentication.components.JwtTokenUtil;
import fr.bio.apiauthentication.dto.MessageResponse;
import fr.bio.apiauthentication.dto.admin.UserModificationRequest;
import fr.bio.apiauthentication.dto.admin.UserStructureResponse;
import fr.bio.apiauthentication.entities.Role;
import fr.bio.apiauthentication.entities.User;
import fr.bio.apiauthentication.enums.Messages;
import fr.bio.apiauthentication.exceptions.not_found.RoleNotFoundException;
import fr.bio.apiauthentication.exceptions.already_exists.UserAlreadyExistsException;
import fr.bio.apiauthentication.repositories.RoleRepository;
import fr.bio.apiauthentication.repositories.UserRepository;
import fr.bio.apiauthentication.services.interfaces.IAdminUserService;
import fr.bio.apiauthentication.services.interfaces.IEmailService;
import fr.bio.apiauthentication.services.interfaces.IJwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class AdminUserService implements IAdminUserService {
    private static final String USER = "User";
    private static final String ROLE = "Role";

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    private final IJwtService jwtService;
    private final IEmailService emailService;

    private final HttpHeadersUtil httpHeadersUtil;
    private final JwtTokenUtil jwtTokenUtil;

    private final UserDetailsService userDetailsService;

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
            throw new UserAlreadyExistsException(Messages.ENTITY_ALREADY_EXISTS.formatMessage(USER, request.email()));

        List<Role> roles = request.roles() != null
                ? request.roles().stream()
                    .map(role -> roleRepository.findByAuthority(role)
                            .orElseThrow(() -> new RoleNotFoundException(Messages.ENTITY_NOT_FOUND.formatMessage(ROLE, role))))
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
                .body(new MessageResponse(Messages.ENTITY_CREATED.formatMessage(USER, request.email())));
    }

    @Override
    public ResponseEntity<MessageResponse> updateUser(
            String token,
            UserModificationRequest request
    ) {
        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new UsernameNotFoundException(Messages.ENTITY_NOT_FOUND.formatMessage(USER, request.email())));

        boolean isModified = false;

        if (request.firstName() != null && !request.firstName().isBlank() && !user.getFirstName().equals(request.firstName())) {
            user.setFirstName(request.firstName());
            isModified = true;
        }

        if (request.lastName() != null && !request.lastName().isBlank() && !user.getLastName().equals(request.lastName())) {
            user.setLastName(request.lastName());
            isModified = true;
        }

        if (isModified)
            userRepository.save(user);

        return ResponseEntity.ok()
                .headers(httpHeadersUtil.createHeaders(token))
                .body(isModified
                        ? new MessageResponse(Messages.ENTITY_UPDATED.formatMessage(USER, request.email()))
                        : new MessageResponse(Messages.ENTITY_NO_MODIFIED.formatMessage(USER, request.email()))
                );
    }

    @Override
    public ResponseEntity<MessageResponse> updateUserStatus(
            String token,
            UserModificationRequest request,
            boolean status
    ) {
        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new UsernameNotFoundException(Messages.ENTITY_NOT_FOUND.formatMessage(USER, request.email())));

        user.setEnabled(status);
        userRepository.save(user);

        return ResponseEntity.ok()
                .headers(httpHeadersUtil.createHeaders(token))
                .body(status
                        ? new MessageResponse(Messages.ENTITY_ACTIVATED.formatMessage(USER, request.email()))
                        : new MessageResponse(Messages.ENTITY_DEACTIVATED.formatMessage(USER, request.email()))
                );
    }
}