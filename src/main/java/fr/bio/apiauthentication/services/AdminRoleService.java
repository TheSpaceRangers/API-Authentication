package fr.bio.apiauthentication.services;

import fr.bio.apiauthentication.components.HttpHeadersUtil;
import fr.bio.apiauthentication.dto.MessageResponse;
import fr.bio.apiauthentication.dto.admin.RoleRequest;
import fr.bio.apiauthentication.dto.admin.RoleStructureResponse;
import fr.bio.apiauthentication.entities.Role;
import fr.bio.apiauthentication.enums.Messages;
import fr.bio.apiauthentication.exceptions.already_exists.RoleAlreadyExistsException;
import fr.bio.apiauthentication.exceptions.not_found.RoleNotFoundException;
import fr.bio.apiauthentication.repositories.RoleRepository;
import fr.bio.apiauthentication.services.interfaces.IAdminRoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class AdminRoleService implements IAdminRoleService {
    private static final String ROLE = "Role";

    private final RoleRepository roleRepository;

    private final HttpHeadersUtil httpHeadersUtil;

    @Override
    public ResponseEntity<List<RoleStructureResponse>> getAllByStatus(
            String token,
            Boolean isEnabled
    ) {
        final List<Role> roles = isEnabled == null
                ? roleRepository.findAll()
                : roleRepository.findAllByEnabled(isEnabled);

        return ResponseEntity.ok()
                .headers(httpHeadersUtil.createHeaders(token))
                .body(RoleStructureResponse.fromRoles(roles));
    }

    @Override
    public ResponseEntity<MessageResponse> newRole(
            String token,
            RoleRequest request
    ) {
        Role role = roleRepository.findByAuthority(request.authority())
                .orElse(null);

        if (role != null)
            throw new RoleAlreadyExistsException(Messages.ENTITY_ALREADY_EXISTS.formatMessage(ROLE, request.authority()));

        role = Role.builder()
                .authority(request.authority())
                .displayName(request.displayName())
                .description(request.description())
                .build();
        roleRepository.save(role);

        return ResponseEntity.ok()
                .headers(httpHeadersUtil.createHeaders(token))
                .body(MessageResponse.fromMessage(Messages.ENTITY_CREATED.formatMessage(ROLE, request.authority())));
    }

    @Override
    public ResponseEntity<MessageResponse> modify(
            String token,
            RoleRequest request
    ) {
        final Role role = roleRepository.findByAuthority(request.authority())
                .orElseThrow(() -> new RoleNotFoundException(Messages.ENTITY_NOT_FOUND.formatMessage(ROLE, request.authority())));

        boolean isModified = false;

        isModified |= Optional.ofNullable(request.displayName())
                .filter(displayName -> !displayName.isBlank() && !displayName.equals(role.getDisplayName()))
                .map(displayName -> {
                    role.setDisplayName(displayName);
                    return true;
                })
                .orElse(false);

        isModified |= Optional.ofNullable(request.description())
                .filter(description -> !description.isBlank() && !description.equals(role.getDescription()))
                .map(description -> {
                    role.setDescription(description);
                    return true;
                })
                .orElse(false);

        if (isModified)
            roleRepository.save(role);

        return ResponseEntity.ok()
                .headers(httpHeadersUtil.createHeaders(token))
                .body(isModified
                        ? new MessageResponse(Messages.ENTITY_UPDATED.formatMessage(ROLE, request.authority()))
                        : new MessageResponse(Messages.ENTITY_NO_MODIFIED.formatMessage(ROLE, request.authority()))
                );
    }

    @Override
    public ResponseEntity<MessageResponse> modifyStatus(
            String token,
            RoleRequest request,
            boolean status
    ) {
        final Role role = roleRepository.findByAuthority(request.authority())
                .orElseThrow(() -> new RoleNotFoundException(Messages.ENTITY_NOT_FOUND.formatMessage(ROLE, request.authority())));

        role.setEnabled(status);
        roleRepository.save(role);

        return ResponseEntity.ok()
                .headers(httpHeadersUtil.createHeaders(token))
                .body(status
                        ? new MessageResponse(Messages.ENTITY_ACTIVATED.formatMessage(ROLE, request.authority()))
                        : new MessageResponse(Messages.ENTITY_DEACTIVATED.formatMessage(ROLE, request.authority()))
                );
    }
}