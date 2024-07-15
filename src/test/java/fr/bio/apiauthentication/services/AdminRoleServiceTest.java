package fr.bio.apiauthentication.services;

import fr.bio.apiauthentication.components.HttpHeadersUtil;
import fr.bio.apiauthentication.dto.MessageResponse;
import fr.bio.apiauthentication.dto.admin.RoleRequest;
import fr.bio.apiauthentication.dto.admin.RoleStructureResponse;
import fr.bio.apiauthentication.entities.Role;
import fr.bio.apiauthentication.entities.User;
import fr.bio.apiauthentication.enums.Messages;
import fr.bio.apiauthentication.exceptions.already_exists.RoleAlreadyExistsException;
import fr.bio.apiauthentication.exceptions.not_found.RoleNotFoundException;
import fr.bio.apiauthentication.repositories.RoleRepository;
import fr.bio.apiauthentication.repositories.UserRepository;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.AfterEach;
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

@DisplayName("Test role admin service")
public class AdminRoleServiceTest {
    private static final LocalDate NOW = LocalDate.now();

    private static final String ROLE = "Role";

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private JwtService jwtService;

    @Mock
    private HttpHeadersUtil httpHeadersUtil;

    @InjectMocks
    private AdminRoleService adminRoleService;

    private User user;

    private HttpHeaders headers;

    private String token;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        user = User.builder()
                .email(RandomStringUtils.randomAlphanumeric(10) + "@test.com")
                .password(RandomStringUtils.randomAlphanumeric(30))
                .firstName(RandomStringUtils.randomAlphanumeric(20))
                .lastName(RandomStringUtils.randomAlphanumeric(20))
                .createdAt(NOW)
                .createdBy(RandomStringUtils.randomAlphanumeric(20))
                .modifiedAt(NOW)
                .modifiedBy(RandomStringUtils.randomAlphanumeric(20))
                .enabled(true)
                .build();
        userRepository.save(user);

        token = jwtService.generateToken(user);

        headers = httpHeadersUtil.createHeaders(token);
    }

    @AfterEach
    void tearDown() {
        roleRepository.deleteAll();
        userRepository.deleteAll();

        user = null;
    }

    @Test
    @DisplayName("Test get roles")
    void testGetAllByStatus() {
        final Role roleActive = generateRole(true);
        final Role roleInactive = generateRole(false);

        final List<RoleStructureResponse> exceptedResponse = List.of(RoleStructureResponse.fromRole(roleActive), RoleStructureResponse.fromRole(roleInactive));

        when(roleRepository.findAll()).thenReturn(List.of(roleActive, roleInactive));
        when(httpHeadersUtil.createHeaders(token)).thenReturn(headers);

        final ResponseEntity<List<RoleStructureResponse>> response = adminRoleService.getAllByStatus(token, null);

        assertThat(response.getBody()).isEqualTo(exceptedResponse);
        assertThat(response.getBody()).usingRecursiveComparison().isEqualTo(exceptedResponse);

        verify(roleRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Test get roles with active status")
    void testGetAllByStatus_Active() {
        final Role roleActive = generateRole(true);

        final List<RoleStructureResponse> exceptedResponse = List.of(RoleStructureResponse.fromRole(roleActive));

        when(roleRepository.findAllByEnabled(true)).thenReturn(List.of(roleActive));
        when(httpHeadersUtil.createHeaders(token)).thenReturn(headers);

        final ResponseEntity<List<RoleStructureResponse>> response = adminRoleService.getAllByStatus(token, true);

        assertThat(response.getBody()).isEqualTo(exceptedResponse);
        assertThat(response.getBody()).usingRecursiveComparison().isEqualTo(exceptedResponse);

        verify(roleRepository, times(1)).findAllByEnabled(true);
    }

    @Test
    @DisplayName("Test get roles with inactive status")
    void testGetAllByStatus_Inactive() {
        final Role roleInactive = generateRole(false);

        final List<RoleStructureResponse> exceptedResponse = List.of(RoleStructureResponse.fromRole(roleInactive));

        when(roleRepository.findAllByEnabled(false)).thenReturn(List.of(roleInactive));
        when(httpHeadersUtil.createHeaders(token)).thenReturn(new HttpHeaders());

        final ResponseEntity<List<RoleStructureResponse>> response = adminRoleService.getAllByStatus(token, false);

        assertThat(response.getBody()).isEqualTo(exceptedResponse);
        assertThat(response.getBody()).usingRecursiveComparison().isEqualTo(exceptedResponse);

        verify(roleRepository, times(1)).findAllByEnabled(false);
    }

    @Test
    @DisplayName("Test get roles but no role")
    void testGetAllByStatus_NoRole() {
        final List<RoleStructureResponse> exceptedResponse = List.of();

        when(roleRepository.findAllByEnabled(true)).thenReturn(List.of());
        when(httpHeadersUtil.createHeaders(token)).thenReturn(headers);

        final ResponseEntity<List<RoleStructureResponse>> response = adminRoleService.getAllByStatus(token, true);

        assertThat(response.getBody()).isEqualTo(exceptedResponse);

        verify(roleRepository, times(1)).findAllByEnabled(true);
    }

    @Test
    @DisplayName("Test create role")
    void testNewRole_Success() {
        final Role role = generateRole(true);

        final RoleRequest request = new RoleRequest(role.getAuthority(), role.getDisplayName(), role.getDescription());

        final MessageResponse exceptedResponse = MessageResponse.fromMessage(Messages.ENTITY_CREATED.formatMessage("Role", role.getAuthority()));

        when(roleRepository.findByAuthority(request.authority())).thenReturn(Optional.empty());
        when(roleRepository.save(any(Role.class))).thenReturn(role);
        when(httpHeadersUtil.createHeaders(token)).thenReturn(headers);

        final ResponseEntity<MessageResponse> response = adminRoleService.newRole(token, request);

        assertThat(response).isNotNull();
        assertThat(response.getBody()).usingRecursiveComparison().isEqualTo(exceptedResponse);

        final ArgumentCaptor<Role> roleCaptor = ArgumentCaptor.forClass(Role.class);
        verify(roleRepository).save(roleCaptor.capture());
        final Role savedRole = roleCaptor.getValue();

        assertThat(savedRole).isNotNull();
        assertThat(savedRole.getAuthority()).isEqualTo(role.getAuthority());
        assertThat(savedRole.getDisplayName()).isEqualTo(role.getDisplayName());
        assertThat(savedRole.getDescription()).isEqualTo(role.getDescription());

        verify(roleRepository, times(1)).findByAuthority(request.authority());
        verify(roleRepository, times(1)).save(any(Role.class));
    }

    @Test
    @DisplayName("Test create role but role existing")
    void testNewRole_RoleExist() {
        final Role role = generateRole(true);

        final RoleRequest request = new RoleRequest(role.getAuthority(), role.getDisplayName(), role.getDescription());

        when(roleRepository.findByAuthority(request.authority())).thenReturn(Optional.of(role));

        assertThrows(RoleAlreadyExistsException.class, () -> adminRoleService.newRole(token, request));

        verify(roleRepository, times(1)).findByAuthority(request.authority());
    }

    @Test
    @DisplayName("Test update role")
    void testModify_Success() {
        final Role role = generateRole(true);

        final String displayName = RandomStringUtils.randomAlphanumeric(20);
        final String description = RandomStringUtils.randomAlphanumeric(20);

        final RoleRequest request = new RoleRequest(role.getAuthority(), displayName,description);
        final MessageResponse exceptedResponse = MessageResponse.fromMessage(Messages.ENTITY_UPDATED.formatMessage(ROLE, role.getAuthority()));

        when(roleRepository.findByAuthority(request.authority())).thenReturn(Optional.of(role));
        when(httpHeadersUtil.createHeaders(token)).thenReturn(headers);

        final ResponseEntity<MessageResponse> response = adminRoleService.modify(token, request);

        assertThat(response).isNotNull();
        assertThat(response.getBody()).usingRecursiveComparison().isEqualTo(exceptedResponse);

        final ArgumentCaptor<Role> roleCaptor = ArgumentCaptor.forClass(Role.class);
        verify(roleRepository).save(roleCaptor.capture());
        final Role savedRole = roleCaptor.getValue();

        assertThat(savedRole).isNotNull();
        assertThat(savedRole.getDisplayName()).isEqualTo(displayName);
        assertThat(savedRole.getDescription()).isEqualTo(description);

        verify(roleRepository, times(1)).findByAuthority(request.authority());
    }

    @Test
    @DisplayName("Test update role")
    void testModify_NoChange() {
        final Role role = generateRole(true);

        final RoleRequest request = new RoleRequest(role.getAuthority(), "", "");
        final MessageResponse exceptedResponse = MessageResponse.fromMessage(Messages.ENTITY_NO_MODIFIED.formatMessage(ROLE, role.getAuthority()));

        when(roleRepository.findByAuthority(request.authority())).thenReturn(Optional.of(role));
        when(httpHeadersUtil.createHeaders(token)).thenReturn(headers);

        final ResponseEntity<MessageResponse> response = adminRoleService.modify(token, request);

        assertThat(response).isNotNull();
        assertThat(response.getBody()).usingRecursiveComparison().isEqualTo(exceptedResponse);

        verify(roleRepository, times(1)).findByAuthority(request.authority());
        verify(roleRepository, never()).save(role);
    }

    @Test
    @DisplayName("Test update role but role not found")
    void testModifyRole_NotFound() {
        final Role role = generateRole(true);

        RoleRequest request = new RoleRequest(role.getAuthority(), role.getDisplayName(), role.getDescription());

        when(roleRepository.findByAuthority(request.authority())).thenReturn(Optional.empty());

        assertThrows(RoleNotFoundException.class, () -> adminRoleService.modify(token, request));

        verify(roleRepository, times(1)).findByAuthority(request.authority());
        verify(roleRepository, never()).save(any(Role.class));
    }

    @Test
    @DisplayName("Test active role status")
    void testModifyStatus_Activated() {
        final Role role = generateRole(false);

        final RoleRequest request = new RoleRequest(role.getAuthority(), "", "");
        final MessageResponse exceptedResponse = MessageResponse.fromMessage(Messages.ENTITY_ACTIVATED.formatMessage(ROLE, role.getAuthority()));

        when(roleRepository.findByAuthority(request.authority())).thenReturn(Optional.of(role));
        when(httpHeadersUtil.createHeaders(token)).thenReturn(new HttpHeaders());

        final ResponseEntity<MessageResponse> response = adminRoleService.modifyStatus(token, request, true);

        assertThat(response).isNotNull();
        assertThat(response.getBody()).usingRecursiveComparison().isEqualTo(exceptedResponse);

        final ArgumentCaptor<Role> roleCaptor = ArgumentCaptor.forClass(Role.class);
        verify(roleRepository).save(roleCaptor.capture());
        final Role savedRole = roleCaptor.getValue();

        assertThat(savedRole).isNotNull();
        assertThat(savedRole.isEnabled()).isTrue();

        verify(roleRepository, times(1)).findByAuthority(request.authority());
    }

    @Test
    @DisplayName("Test deactivate role status")
    void testModifyStatus_Deactivated() {
        final Role role = generateRole(true);

        final RoleRequest request = new RoleRequest(role.getAuthority(), "", "");
        final MessageResponse exceptedResponse = MessageResponse.fromMessage(Messages.ENTITY_DEACTIVATED.formatMessage(ROLE, role.getAuthority()));

        when(roleRepository.findByAuthority(request.authority())).thenReturn(Optional.of(role));
        when(httpHeadersUtil.createHeaders(token)).thenReturn(new HttpHeaders());

        final ResponseEntity<MessageResponse> response = adminRoleService.modifyStatus(token, request, false);

        assertThat(response).isNotNull();
        assertThat(response.getBody()).usingRecursiveComparison().isEqualTo(exceptedResponse);

        final ArgumentCaptor<Role> roleCaptor = ArgumentCaptor.forClass(Role.class);
        verify(roleRepository).save(roleCaptor.capture());
        final Role savedRole = roleCaptor.getValue();

        assertThat(savedRole).isNotNull();
        assertThat(savedRole.isEnabled()).isFalse();

        verify(roleRepository, times(1)).findByAuthority(request.authority());
    }

    @Test
    @DisplayName("Test update role status but role not found")
    void testModifyRoleStatus_NotFound() {
        final Role role = generateRole(true);
        final RoleRequest request = new RoleRequest(role.getAuthority(),role.getDisplayName(), role.getDescription());

        when(roleRepository.findByAuthority(request.authority())).thenReturn(Optional.empty());

        assertThrows(RoleNotFoundException.class, () -> adminRoleService.modifyStatus(token, request, true));

        verify(roleRepository, times(1)).findByAuthority(request.authority());
    }

    private Role generateRole(boolean enabled) {
        return Role.builder()
                .authority(RandomStringUtils.randomAlphanumeric(10).toUpperCase())
                .displayName(RandomStringUtils.randomAlphanumeric(20))
                .description(RandomStringUtils.randomAlphanumeric(20))
                .modifiedAt(NOW)
                .modifiedBy(RandomStringUtils.randomAlphanumeric(20))
                .enabled(enabled)
                .users(List.of(user))
                .build();
    }
}