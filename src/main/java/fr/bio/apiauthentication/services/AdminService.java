package fr.bio.apiauthentication.services;

import fr.bio.apiauthentication.components.HttpHeadersUtil;
import fr.bio.apiauthentication.dto.MessageResponse;
import fr.bio.apiauthentication.dto.admin.UpdateRoleRequest;
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
            UpdateRoleRequest request
    ) {
        Role role = roleRepository.findByAuthority(request.authority())
                .orElseThrow(() -> new RoleNotFoundException("Role " + request.authority() + " not found"));

        if (request.displayName() != null && !request.displayName().isBlank() && !role.getDisplayName().equals(request.displayName()))
            role.setDisplayName(request.displayName());

        if (request.description() != null && !request.description().isBlank() && !role.getDescription().equals(request.description()))
            role.setDescription(request.description());

        roleRepository.save(role);

        return ResponseEntity.ok()
                .headers(httpHeadersUtil.createHeaders(token))
                .body(new MessageResponse("Role " + request.authority() + " has been updated"));
    }
}
