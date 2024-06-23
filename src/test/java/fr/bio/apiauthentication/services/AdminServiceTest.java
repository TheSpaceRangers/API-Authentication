package fr.bio.apiauthentication.services;

import fr.bio.apiauthentication.components.HttpHeadersUtil;
import fr.bio.apiauthentication.dto.MessageResponse;
import fr.bio.apiauthentication.dto.admin.UpdateRoleRequest;
import fr.bio.apiauthentication.entities.Role;
import fr.bio.apiauthentication.exceptions.InvalidCredentialsException;
import fr.bio.apiauthentication.exceptions.RoleNotFoundException;
import fr.bio.apiauthentication.repositories.RoleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@DisplayName("Test admin service")
public class AdminServiceTest {
    @Mock
    private RoleRepository roleRepository;

    @Mock
    private HttpHeadersUtil httpHeadersUtil;

    @InjectMocks
    private AdminService adminService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("Test update role")
    void testUpdateRole() {
        String token = "token";
        UpdateRoleRequest request = new UpdateRoleRequest("ROLE_USER", "Utilisateur", "");

        Role role = Role.builder()
                .authority("ROLE_USER")
                .displayName("Old Utilisateur")
                .description("Description")
                .build();

        when(roleRepository.findByAuthority(request.authority())).thenReturn(Optional.of(role));
        when(httpHeadersUtil.createHeaders(token)).thenReturn(new HttpHeaders());

        ResponseEntity<MessageResponse> response = adminService.updateRole(token, request);

        assertThat(response.getBody().getMessage()).isEqualTo("Role " + request.authority() + " has been updated");

        verify(roleRepository, times(1)).save(role);
        verify(httpHeadersUtil).createHeaders(token);
    }

    @Test
    @DisplayName("Test update role but role not found")
    void testUpdateRole_RoleNotFound() {
        String token = "token";
        UpdateRoleRequest request = new UpdateRoleRequest("ROLE_UNKNOWN", "Unknown", "");

        when(roleRepository.findByAuthority(request.authority())).thenReturn(Optional.empty());

        assertThrows(RoleNotFoundException.class, () -> adminService.updateRole(token, request));

        verify(roleRepository, times(1)).findByAuthority(request.authority());
        verify(roleRepository, never()).save(any(Role.class));
    }
}
