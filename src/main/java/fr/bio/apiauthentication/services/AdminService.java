package fr.bio.apiauthentication.services;

import fr.bio.apiauthentication.components.HttpHeadersUtil;
import fr.bio.apiauthentication.dto.MessageResponse;
import fr.bio.apiauthentication.dto.admin.RoleModificationRequest;
import fr.bio.apiauthentication.entities.Role;
import fr.bio.apiauthentication.exceptions.RoleNotFoundException;
import fr.bio.apiauthentication.repositories.RoleRepository;
import fr.bio.apiauthentication.services.interfaces.IAdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class AdminService implements IAdminService {
    private final RoleRepository roleRepository;

    private final HttpHeadersUtil httpHeadersUtil;

    @Override
    public ResponseEntity<MessageResponse> updateRole(
            String token,
            RoleModificationRequest request
    ) {
        Role role = roleRepository.findByAuthority(request.authority())
                .orElseThrow(() -> new RoleNotFoundException("Role " + request.authority() + " not found"));

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
                        ? new MessageResponse("Role " + request.authority() + " has been updated")
                        : new MessageResponse("No modifications were made to the role " + request.authority())
                );
    }

    @Override
    public ResponseEntity<MessageResponse> deactivateRole(
            String token,
            RoleModificationRequest request
    ) {
        Role role = roleRepository.findByAuthority(request.authority())
                .orElseThrow(() -> new RoleNotFoundException("Role " + request.authority() + " not found"));

        role.setEnabled(false);
        roleRepository.save(role);

        return ResponseEntity.ok()
                .headers(httpHeadersUtil.createHeaders(token))
                .body(new MessageResponse("Role " + request.authority() + " has been deactivated"));
    }
}
