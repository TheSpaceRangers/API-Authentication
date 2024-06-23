package fr.bio.apiauthentication.services;

import fr.bio.apiauthentication.components.HttpHeadersUtil;
import fr.bio.apiauthentication.dto.MessageResponse;
import fr.bio.apiauthentication.dto.admin.RoleModificationRequest;
import fr.bio.apiauthentication.dto.admin.RoleStructureResponse;
import fr.bio.apiauthentication.entities.Role;
import fr.bio.apiauthentication.enums.Messages;
import fr.bio.apiauthentication.exceptions.RoleAlreadyExistsException;
import fr.bio.apiauthentication.exceptions.RoleNotFoundException;
import fr.bio.apiauthentication.repositories.RoleRepository;
import fr.bio.apiauthentication.services.interfaces.IAdminRoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class AdminRoleService implements IAdminRoleService {
    private final RoleRepository roleRepository;

    private final HttpHeadersUtil httpHeadersUtil;

    @Override
    public ResponseEntity<List<RoleStructureResponse>> getAllRolesByStatus(
            String token,
            Boolean isActive
    ) {
        List<Role> roles = isActive == null
                ? roleRepository.findAll()
                : roleRepository.findAllByEnabled(isActive);

        System.out.println("Roles : " + roles);

        List<RoleStructureResponse> responses = roles != null
                ? roles.stream()
                    .map(RoleStructureResponse::fromRole)
                    .toList()
                : List.of();

        return ResponseEntity.ok()
                .headers(httpHeadersUtil.createHeaders(token))
                .body(responses);
    }

    @Override
    public ResponseEntity<MessageResponse> createRole(
            String token,
            RoleModificationRequest request
    ) {
        Role role = roleRepository.findByAuthority(request.authority())
                .orElse(null);

        if (role != null)
            throw new RoleAlreadyExistsException(Messages.ROLE_ALREADY_EXISTS.formatMessage(request.authority()));

        role = Role.builder()
                .authority(request.authority())
                .displayName(request.displayName())
                .description(request.description())
                .build();

        roleRepository.save(role);

        return ResponseEntity.ok()
                .headers(httpHeadersUtil.createHeaders(token))
                .body(new MessageResponse(Messages.ROLE_CREATED.formatMessage(request.authority())));
    }

    @Override
    public ResponseEntity<MessageResponse> updateRole(
            String token,
            RoleModificationRequest request
    ) {
        Role role = roleRepository.findByAuthority(request.authority())
                .orElseThrow(() -> new RoleNotFoundException(Messages.ROLE_NOT_FOUND.formatMessage(request.authority())));

        boolean isModified = false;

        if (request.displayName() != null && !request.displayName().isBlank() && !role.getDisplayName().equals(request.displayName())) {
            role.setDisplayName(request.displayName());
            isModified = true;
        }

        if (request.description() != null && !request.description().isBlank() && !role.getDescription().equals(request.description())) {
            role.setDescription(request.description());
            isModified = true;
        }

        if (isModified)
            roleRepository.save(role);

        return ResponseEntity.ok()
                .headers(httpHeadersUtil.createHeaders(token))
                .body(isModified
                        ? new MessageResponse(Messages.ROLE_UPDATED.formatMessage(request.authority()))
                        : new MessageResponse(Messages.ROLE_NO_MODIFIED.formatMessage(request.authority()))
                );
    }

    @Override
    public ResponseEntity<MessageResponse> updateRoleStatus(String token, RoleModificationRequest request, boolean status) {
        Role role = roleRepository.findByAuthority(request.authority())
                .orElseThrow(() -> new RoleNotFoundException(Messages.ROLE_NOT_FOUND.formatMessage(request.authority())));

        role.setEnabled(status);
        roleRepository.save(role);

        return ResponseEntity.ok()
                .headers(httpHeadersUtil.createHeaders(token))
                .body(status
                        ? new MessageResponse(Messages.ROLE_ACTIVATED.formatMessage(request.authority()))
                        : new MessageResponse(Messages.ROLE_DEACTIVATED.formatMessage(request.authority()))
                );
    }
}
