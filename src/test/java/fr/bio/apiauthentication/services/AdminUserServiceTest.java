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
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@DisplayName("Test user admin service")
public class AdminUserServiceTest {
    private static final LocalDate NOW = LocalDate.now();

    private static final String USER = "User";

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private JwtService jwtService;

    @Mock
    private HttpHeadersUtil httpHeadersUtil;

    @InjectMocks
    private AdminUserService adminUserService;

    private User userActive;
    private User userInactive;
    private Role role;

    private HttpHeaders headers;

    private String token;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        role = Role.builder()
                .authority(RandomStringUtils.randomAlphanumeric(5).toUpperCase())
                .displayName(RandomStringUtils.randomAlphanumeric(20))
                .description(RandomStringUtils.randomAlphanumeric(20))
                .modifiedAt(NOW)
                .modifiedBy(RandomStringUtils.randomAlphanumeric(30))
                .enabled(true)
                .build();
        roleRepository.save(role);

        userActive = generateUser(true);
        userInactive = generateUser(false);

        token = jwtService.generateToken(userActive);

        headers = httpHeadersUtil.createHeaders(token);
    }

    @Test
    @DisplayName("Test get all users")
    public void testGetAllUsers() {
        final List<UserStructureResponse> exceptedUsers = UserStructureResponse.fromUsers(List.of(userActive, userInactive));

        when(userRepository.findAll()).thenReturn(List.of(userActive, userInactive));
        when(httpHeadersUtil.createHeaders(token)).thenReturn(headers);

        final ResponseEntity<List<UserStructureResponse>> response = adminUserService.getAllUsers(token);

        assertThat(response).isNotNull();
        assertThat(response.getBody()).isEqualTo(exceptedUsers);
        assertThat(response.getBody()).usingRecursiveComparison().isEqualTo(exceptedUsers);

        verify(userRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Test create user")
    public void testNewUser_Success() {
        final UserRequest request = new UserRequest(userActive.getEmail(), userActive.getFirstName(), userActive.getLastName(), userActive.getRoles().stream().map(Role::getAuthority).toList());
        final MessageResponse exceptedResponse = MessageResponse.fromMessage(Messages.ENTITY_CREATED.formatMessage(USER, userActive.getEmail()));

        when(userRepository.findByEmail(userActive.getEmail())).thenReturn(Optional.empty());
        when(roleRepository.findByAuthority(role.getAuthority())).thenReturn(Optional.of(role));
        when(httpHeadersUtil.createHeaders(token)).thenReturn(headers);

        final ResponseEntity<MessageResponse> response = adminUserService.newUser(token, request);

        final ArgumentCaptor<User> roleCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(roleCaptor.capture());
        final User savedUser = roleCaptor.getValue();

        assertThat(response).isNotNull();
        assertThat(response.getBody()).usingRecursiveComparison().isEqualTo(exceptedResponse);

        assertThat(savedUser).isNotNull();
        assertThat(savedUser.getEmail()).isEqualTo(userActive.getEmail());
        assertThat(savedUser.getFirstName()).isEqualTo(userActive.getFirstName());
        assertThat(savedUser.getLastName()).isEqualTo(userActive.getLastName());
        assertThat(savedUser.getRoles()).usingRecursiveComparison().isEqualTo(List.of(role));

        verify(roleRepository, times(1)).findByAuthority(anyString());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    @DisplayName("Test create user but user already exists")
    public void testNewUser_UserAlreadyExists() {
        final UserRequest request = new UserRequest(userActive.getEmail(), userActive.getFirstName(), userActive.getLastName(), userActive.getRoles().stream().map(Role::getAuthority).toList());

        when(userRepository.findByEmail(request.email())).thenReturn(Optional.of(userActive));

        assertThrows(UserAlreadyExistsException.class, () -> adminUserService.newUser(token, request));

        verify(userRepository, times(1)).findByEmail(userActive.getEmail());
    }

    @Test
    @DisplayName("Test create user but role not found")
    public void testNewUser_RoleNotFount() {
        final UserRequest request = new UserRequest(userInactive.getEmail(), userInactive.getFirstName(), userInactive.getLastName(), userInactive.getRoles().stream().map(Role::getAuthority).toList());

        when(userRepository.findByEmail(userInactive.getEmail())).thenReturn(Optional.empty());
        when(roleRepository.findByAuthority(role.getAuthority())).thenReturn(Optional.empty());

        assertThrows(RoleNotFoundException.class, () -> adminUserService.newUser(token, request));

        verify(userRepository, times(1)).findByEmail(request.email());
        verify(roleRepository, times(1)).findByAuthority(anyString());
    }

    @Test
    @DisplayName("Test update user")
    void testModify_Success() {
        final String firstName = RandomStringUtils.randomAlphanumeric(20);
        final String lastName = RandomStringUtils.randomAlphanumeric(20);

        final UserRequest request = new UserRequest(userInactive.getEmail(), firstName, lastName, List.of());
        final MessageResponse exceptedResponse = MessageResponse.fromMessage(Messages.ENTITY_UPDATED.formatMessage(USER, userInactive.getEmail()));

        when(userRepository.findByEmail(userInactive.getEmail())).thenReturn(Optional.of(userInactive));
        when(httpHeadersUtil.createHeaders(token)).thenReturn(headers);

        ResponseEntity<MessageResponse> response = adminUserService.modify(token, request);

        final ArgumentCaptor<User> roleCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(roleCaptor.capture());
        final User savedUser = roleCaptor.getValue();

        assertThat(response).isNotNull();
        assertThat(response.getBody()).usingRecursiveComparison().isEqualTo(exceptedResponse);

        assertThat(savedUser).isNotNull();
        assertThat(savedUser.getFirstName()).isEqualTo(firstName);
        assertThat(savedUser.getLastName()).isEqualTo(lastName);

        verify(userRepository, times(1)).save(userInactive);
    }

    @Test
    @DisplayName("Test update user but user not found")
    void testModifyNotFound() {
        final String firstName = RandomStringUtils.randomAlphanumeric(20);
        final String lastName = RandomStringUtils.randomAlphanumeric(20);

        final UserRequest request = new UserRequest(userInactive.getEmail(), firstName, lastName, List.of());

        when(userRepository.findByEmail(request.email())).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class, () -> adminUserService.modify(token, request));

        verify(userRepository, times(1)).findByEmail(userInactive.getEmail());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("Test active user status")
    void testModifyUserStatus_Activated() {
        final UserRequest request = new UserRequest(userInactive.getEmail(), "", "", List.of());
        final MessageResponse exceptedResponse = MessageResponse.fromMessage(Messages.ENTITY_ACTIVATED.formatMessage(USER, userInactive.getEmail()));

        when(userRepository.findByEmail(userInactive.getEmail())).thenReturn(Optional.of(userInactive));
        when(httpHeadersUtil.createHeaders(token)).thenReturn(headers);

        final ResponseEntity<MessageResponse> response = adminUserService.modifyStatus(token, request, true);

        final ArgumentCaptor<User> roleCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(roleCaptor.capture());
        final User savedUser = roleCaptor.getValue();

        assertThat(response).isNotNull();
        assertThat(response.getBody()).usingRecursiveComparison().isEqualTo(exceptedResponse);

        assertThat(savedUser).isNotNull();
        assertThat(savedUser.isEnabled()).isTrue();

        verify(roleRepository, times(1)).save(role);
    }

    @Test
    @DisplayName("Test deactivate user status")
    void testModifyUserStatus_Deactivated() {
        final UserRequest request = new UserRequest(userActive.getEmail(), "", "", List.of());
        final MessageResponse exceptedResponse = MessageResponse.fromMessage(Messages.ENTITY_ACTIVATED.formatMessage(USER, userActive.getEmail()));

        when(userRepository.findByEmail(userActive.getEmail())).thenReturn(Optional.of(userActive));
        when(httpHeadersUtil.createHeaders(token)).thenReturn(headers);

        final ResponseEntity<MessageResponse> response = adminUserService.modifyStatus(token, request, false);

        final ArgumentCaptor<User> roleCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(roleCaptor.capture());
        final User savedUser = roleCaptor.getValue();

        assertThat(response).isNotNull();
        assertThat(response.getBody()).usingRecursiveComparison().isEqualTo(exceptedResponse);

        assertThat(savedUser).isNotNull();
        assertThat(savedUser.isEnabled()).isFalse();

        verify(roleRepository, times(1)).save(role);
    }

    @Test
    @DisplayName("Test update role status but role not found")
    void testModifyRoleStatus_NotFound() {
        UserRequest request = new UserRequest(userInactive.getEmail(), "", "", List.of());

        when(userRepository.findByEmail(userInactive.getEmail())).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class, () -> adminUserService.modifyStatus(token, request, true));

        verify(userRepository, times(1)).findByEmail(userInactive.getEmail());
        verify(userRepository, never()).save(any(User.class));
    }

    private User generateUser(boolean enabled) {
        return User.builder()
                .email(RandomStringUtils.randomAlphanumeric(10) + "@test.com")
                .password(RandomStringUtils.randomAlphanumeric(30))
                .firstName(RandomStringUtils.randomAlphanumeric(20))
                .lastName(RandomStringUtils.randomAlphanumeric(20))
                .createdAt(NOW)
                .createdBy(RandomStringUtils.randomAlphanumeric(20))
                .modifiedAt(NOW)
                .modifiedBy(RandomStringUtils.randomAlphanumeric(20))
                .enabled(enabled)
                .roles(List.of(role))
                .build();
    }
}