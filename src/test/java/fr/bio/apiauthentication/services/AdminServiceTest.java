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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.util.List;
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
    @DisplayName("Test get roles")
    void testGetAllRolesByStatus() {
        String token = "token";

        Role roleActive = Role.builder()
                .authority("ADMIN")
                .displayName("ADMIN")
                .description("ADMIN")
                .modifiedAt(LocalDate.now())
                .modifiedBy("System")
                .enabled(true)
                .users(null)
                .build();

        Role roleInactive = Role.builder()
                .authority("USER")
                .displayName("USER")
                .description("USER")
                .modifiedAt(LocalDate.now())
                .modifiedBy("System")
                .enabled(false)
                .users(null)
                .build();
        List<RoleStructureResponse> actualResponse = List.of(RoleStructureResponse.fromRole(roleActive), RoleStructureResponse.fromRole(roleInactive));

        when(roleRepository.findAll()).thenReturn(List.of(roleActive, roleInactive));
        when(httpHeadersUtil.createHeaders(token)).thenReturn(new HttpHeaders());

        ResponseEntity<List<RoleStructureResponse>> response = adminService.getAllRolesByStatus(token, null);

        assertThat(response.getBody()).isEqualTo(actualResponse);

        verify(roleRepository, times(1)).findAll();
        verify(httpHeadersUtil).createHeaders(token);
    }

    @Test
    @DisplayName("Test get roles with active status")
    void testGetAllRolesByStatus_Active() {
        String token = "token";

        Role roleActive = Role.builder()
                .authority("ADMIN")
                .displayName("ADMIN")
                .description("ADMIN")
                .modifiedAt(LocalDate.now())
                .modifiedBy("System")
                .enabled(true)
                .users(null)
                .build();

        Role roleInactive = Role.builder()
                .authority("USER")
                .displayName("USER")
                .description("USER")
                .modifiedAt(LocalDate.now())
                .modifiedBy("System")
                .enabled(false)
                .users(null)
                .build();
        List<RoleStructureResponse> actualResponse = List.of(RoleStructureResponse.fromRole(roleActive));

        when(roleRepository.findAllByEnabled(true)).thenReturn(List.of(roleActive));
        when(httpHeadersUtil.createHeaders(token)).thenReturn(new HttpHeaders());

        ResponseEntity<List<RoleStructureResponse>> response = adminService.getAllRolesByStatus(token, true);

        assertThat(response.getBody()).isEqualTo(actualResponse);

        verify(roleRepository, times(1)).findAllByEnabled(true);
        verify(httpHeadersUtil).createHeaders(token);
    }

    @Test
    @DisplayName("Test get roles with inactive status")
    void testGetAllRolesByStatus_Inactive() {
        String token = "token";

        Role roleActive = Role.builder()
                .authority("ADMIN")
                .displayName("ADMIN")
                .description("ADMIN")
                .modifiedAt(LocalDate.now())
                .modifiedBy("System")
                .enabled(true)
                .users(null)
                .build();

        Role roleInactive = Role.builder()
                .authority("USER")
                .displayName("USER")
                .description("USER")
                .modifiedAt(LocalDate.now())
                .modifiedBy("System")
                .enabled(false)
                .users(null)
                .build();
        List<RoleStructureResponse> actualResponse = List.of(RoleStructureResponse.fromRole(roleInactive));

        when(roleRepository.findAllByEnabled(false)).thenReturn(List.of(roleInactive));
        when(httpHeadersUtil.createHeaders(token)).thenReturn(new HttpHeaders());

        ResponseEntity<List<RoleStructureResponse>> response = adminService.getAllRolesByStatus(token, false);

        assertThat(response.getBody()).isEqualTo(actualResponse);

        verify(roleRepository, times(1)).findAllByEnabled(false);
        verify(httpHeadersUtil).createHeaders(token);
    }

    @Test
    @DisplayName("Test get roles but no role")
    void testGetAllRoles_ByStatus_NoRole() {
        String token = "token";

        List<RoleStructureResponse> actualResponse = List.of();

        when(roleRepository.findAllByEnabled(true)).thenReturn(List.of());
        when(httpHeadersUtil.createHeaders(token)).thenReturn(new HttpHeaders());

        ResponseEntity<List<RoleStructureResponse>> response = adminService.getAllRolesByStatus(token, true);

        assertThat(response.getBody()).isEqualTo(actualResponse);

        verify(roleRepository, times(1)).findAllByEnabled(true);
        verify(httpHeadersUtil).createHeaders(token);
    }

    @Test
    @DisplayName("Test create role")
    void testCreateRole_Success() {
        String token = "token";

        RoleModificationRequest request = new RoleModificationRequest("NEW_ROLE", "NEW_ROLE", "NEW_ROLE");

        Role role = Role.builder()
                .authority("NEW_ROLE")
                .displayName("NEW_ROLE")
                .description("NEW_ROLE")
                .modifiedBy("System")
                .enabled(true)
                .users(null)
                .build();

        when(roleRepository.findByAuthority(request.authority())).thenReturn(Optional.empty());
        when(roleRepository.save(any(Role.class))).thenReturn(role);
        when(httpHeadersUtil.createHeaders(token)).thenReturn(new HttpHeaders());

        ResponseEntity<MessageResponse> response = adminService.createRole(token, request);

        ArgumentCaptor<Role> roleCaptor = ArgumentCaptor.forClass(Role.class);
        verify(roleRepository).save(roleCaptor.capture());
        Role savedRole = roleCaptor.getValue();

        assertThat(response.getBody().getMessage()).isEqualTo(Messages.ROLE_CREATED.formatMessage(request.authority()));

        assertThat(savedRole).isNotNull();
        assertThat(savedRole.getAuthority()).isEqualTo(request.authority());
        assertThat(savedRole.getDisplayName()).isEqualTo(request.displayName());
        assertThat(savedRole.getDescription()).isEqualTo(request.description());

        verify(roleRepository, times(1)).findByAuthority(request.authority());
        verify(roleRepository, times(1)).save(any(Role.class));
        verify(httpHeadersUtil).createHeaders(token);
    }

    @Test
    @DisplayName("Test create role but role existing")
    void testCreateRole_RoleExist() {
        String token = "token";

        RoleModificationRequest request = new RoleModificationRequest("NEW_ROLE", "NEW_ROLE", "NEW_ROLE");

        Role role = Role.builder()
                .authority("NEW_ROLE")
                .displayName("NEW_ROLE")
                .description("NEW_ROLE")
                .modifiedBy("System")
                .enabled(true)
                .users(null)
                .build();

        when(roleRepository.findByAuthority(request.authority())).thenReturn(Optional.of(role));

        assertThrows(RoleAlreadyExistsException.class, () -> adminService.createRole(token, request));

        verify(roleRepository, times(1)).findByAuthority(request.authority());
    }

    @Test
    @DisplayName("Test update role")
    void testUpdateRole_Success() {
        String token = "token";
        RoleModificationRequest request = new RoleModificationRequest("ROLE_USER", "Utilisateur", "");

        Role role = Role.builder()
                .authority("ROLE_USER")
                .displayName("Old Utilisateur")
                .description("Description")
                .build();

        when(roleRepository.findByAuthority(request.authority())).thenReturn(Optional.of(role));
        when(httpHeadersUtil.createHeaders(token)).thenReturn(new HttpHeaders());

        ResponseEntity<MessageResponse> response = adminService.updateRole(token, request);

        Role savedRole = roleRepository.findByAuthority(request.authority())
                        .orElse(null);

        assertThat(response.getBody().getMessage()).isEqualTo(Messages.ROLE_UPDATED.formatMessage(request.authority()));

        assertThat(savedRole).isNotNull();
        assertThat(savedRole.getDisplayName()).isEqualTo(request.displayName());

        verify(roleRepository, times(1)).save(role);
        verify(httpHeadersUtil).createHeaders(token);
    }

    @Test
    @DisplayName("Test update role but role not found")
    void testUpdateRole_RoleNotFound() {
        String token = "token";
        RoleModificationRequest request = new RoleModificationRequest("ROLE_UNKNOWN", "Unknown", "");

        when(roleRepository.findByAuthority(request.authority())).thenReturn(Optional.empty());

        assertThrows(RoleNotFoundException.class, () -> adminService.updateRole(token, request));

        verify(roleRepository, times(1)).findByAuthority(request.authority());
        verify(roleRepository, never()).save(any(Role.class));
    }

    @Test
    @DisplayName("Test update role status")
    void testUpdateRoleStatus_Success() {
        String token = "token";
        RoleModificationRequest request = new RoleModificationRequest("ROLE_USER", "", "");

        Role role = Role.builder()
                .authority("ROLE_USER")
                .displayName("Old Utilisateur")
                .description("Description")
                .enabled(true)
                .build();

        when(roleRepository.findByAuthority(request.authority())).thenReturn(Optional.of(role));
        when(httpHeadersUtil.createHeaders(token)).thenReturn(new HttpHeaders());

        ResponseEntity<MessageResponse> response = adminService.updateRoleStatus(token, request, false);

        Role savedRole = roleRepository.findByAuthority(request.authority())
                .orElse(null);

        assertThat(response.getBody().getMessage()).isEqualTo(Messages.ROLE_DEACTIVATED.formatMessage(request.authority()));
        assertThat(savedRole).isNotNull();
        assertThat(savedRole.isEnabled()).isFalse();


        verify(roleRepository, times(1)).save(role);
        verify(httpHeadersUtil).createHeaders(token);
    }

    @Test
    @DisplayName("Test update role status but role not found")
    void testUpdateRoleStatus_RoleNotFound() {
        String token = "token";
        RoleModificationRequest request = new RoleModificationRequest("ROLE_UNKNOWN", "Unknown", "");

        when(roleRepository.findByAuthority(request.authority())).thenThrow(new RoleNotFoundException(Messages.ROLE_NOT_FOUND.formatMessage(request.authority())));

        assertThrows(RoleNotFoundException.class, () -> adminService.updateRoleStatus(token, request, true));

        verify(roleRepository, times(1)).findByAuthority(request.authority());
        verify(roleRepository, never()).save(any(Role.class));
    }
}
