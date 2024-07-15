package fr.bio.apiauthentication.services;

import fr.bio.apiauthentication.components.HttpHeadersUtil;
import fr.bio.apiauthentication.dto.MessageResponse;
import fr.bio.apiauthentication.dto.admin.UserRolesRequest;
import fr.bio.apiauthentication.entities.Role;
import fr.bio.apiauthentication.entities.User;
import fr.bio.apiauthentication.enums.Messages;
import fr.bio.apiauthentication.exceptions.not_found.RoleNotFoundException;
import fr.bio.apiauthentication.repositories.RoleRepository;
import fr.bio.apiauthentication.repositories.UserRepository;
import fr.bio.apiauthentication.services.interfaces.IAdminUserRolesService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class AdminUserRolesService implements IAdminUserRolesService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    private final HttpHeadersUtil httpHeadersUtil;

    @Override
    public ResponseEntity<MessageResponse> modifyUserRoles(
            String token,
            UserRolesRequest request
    ) {
        final User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new UsernameNotFoundException(Messages.ENTITY_NOT_FOUND.formatMessage("User", request.email())));

        final List<Role> rolesToAdd = roleRepository.findAllByAuthorityIn(request.rolesToAdd());
        if (rolesToAdd.size() != request.rolesToAdd().size()) {
            final List<String> missingRoles = request.rolesToAdd().stream()
                    .filter(role -> rolesToAdd.stream().noneMatch(r -> r.getAuthority().equals(role)))
                    .toList();
            throw new RoleNotFoundException(Messages.ENTITY_NOT_FOUND.formatMessage("Roles", String.join(", ", missingRoles)));
        }

        final List<Role> rolesToRemove = roleRepository.findAllByAuthorityIn(request.rolesToRemove());
        if (rolesToRemove.size() != request.rolesToRemove().size()) {
            final List<String> missingRole = request.rolesToRemove().stream()
                    .filter(role -> rolesToRemove.stream().noneMatch(r -> r.getAuthority().equals(role)))
                    .toList();
            throw new RoleNotFoundException(Messages.ENTITY_NOT_FOUND.formatMessage("Roles", String.join(", ", missingRole)));
        }

        user.getRoles().addAll(rolesToAdd);
        user.getRoles().removeAll(rolesToRemove);

        userRepository.save(user);

        return ResponseEntity.ok()
                .headers(httpHeadersUtil.createHeaders(token))
                .body(MessageResponse.fromMessage(Messages.ENTITY_UPDATED.formatMessage("User Roles", user.getEmail())));
    }

    @Override
    public ResponseEntity<List<ResponseEntity<MessageResponse>>>  modifyUsersRoles(
            String token,
            List<UserRolesRequest> requests
    ) {
        final List<ResponseEntity<MessageResponse>> responses = requests.stream()
                .map(request -> modifyUserRoles(token, request))
                .toList();

        return ResponseEntity.ok()
                .headers(httpHeadersUtil.createHeaders(token))
                .body(responses);
    }
}