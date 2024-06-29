package fr.bio.apiauthentication.services;

import fr.bio.apiauthentication.components.HttpHeadersUtil;
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
    private static final String USER = "User";

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private HttpHeadersUtil httpHeadersUtil;

    @InjectMocks
    private AdminUserService adminUserService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("Test get all users")
    public void testGetAllUsers() {
        String token = "This is a token";

        Role role = Role.builder()
                .authority("ROLE_TEST")
                .build();

        User user_1 = User.builder()
                .email("user_1@test.com")
                .firstName("user_1")
                .lastName("user_1")
                .createdAt(LocalDate.now())
                .createdBy("system")
                .modifiedAt(LocalDate.now())
                .modifiedBy("system")
                .enabled(true)
                .roles(List.of(role))
                .build();
        User user_2 = User.builder()
                .email("user_2@test.com")
                .firstName("user_2")
                .lastName("user_2")
                .createdAt(LocalDate.now())
                .createdBy("system")
                .modifiedAt(LocalDate.now())
                .modifiedBy("system")
                .enabled(true)
                .roles(List.of(role))
                .build();
        List<UserStructureResponse> users = List.of(UserStructureResponse.fromUser(user_1), UserStructureResponse.fromUser(user_2));

        when(userRepository.findAll()).thenReturn(List.of(user_1, user_2));
        when(httpHeadersUtil.createHeaders(token)).thenReturn(new HttpHeaders());

        ResponseEntity<List<UserStructureResponse>> response = adminUserService.getAllUsers(token);

        assertThat(response.getBody()).isEqualTo(users);

        verify(userRepository, times(1)).findAll();
        verify(httpHeadersUtil).createHeaders(token);
    }

    @Test
    @DisplayName("Test create user")
    public void testCreateUser_Success() {
        String token = "This is a token";

        Role role = Role.builder()
                .authority("ROLE_TEST")
                .build();

        User user_1 = User.builder()
                .email("user_1@test.com")
                .firstName("user_1")
                .lastName("user_1")
                .roles(List.of(role))
                .build();
        UserModificationRequest request = new UserModificationRequest("user_1@test.com", "user_1", "user_1", List.of(role.getAuthority()));

        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());
        when(roleRepository.findByAuthority(anyString())).thenReturn(Optional.of(role));
        when(httpHeadersUtil.createHeaders(token)).thenReturn(new HttpHeaders());

        ResponseEntity<MessageResponse> response = adminUserService.createUser(token, request);

        ArgumentCaptor<User> roleCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(roleCaptor.capture());
        User savedUser = roleCaptor.getValue();

        assertThat(response.getBody().getMessage()).isEqualTo(Messages.ENTITY_CREATED.formatMessage(USER, request.email()));

        assertThat(savedUser).isNotNull();
        assertThat(savedUser.getEmail()).isEqualTo(request.email());
        assertThat(savedUser.getFirstName()).isEqualTo(request.firstName());
        assertThat(savedUser.getLastName()).isEqualTo(request.lastName());
        assertThat(savedUser.getRoles().iterator().next()).isEqualTo(role);

        verify(roleRepository, times(1)).findByAuthority(anyString());
        verify(userRepository, times(1)).save(user_1);
        verify(httpHeadersUtil).createHeaders(token);
    }

    @Test
    @DisplayName("Test create user but user already exists")
    public void testCreateUser_UserAlreadyExists() {
        String token = "This is a token";

        Role role = Role.builder()
                .authority("ROLE_TEST")
                .build();

        User user_1 = User.builder()
                .email("user_1@test.com")
                .firstName("user_1")
                .lastName("user_1")
                .roles(List.of(role))
                .build();
        UserModificationRequest request = new UserModificationRequest("user_1@test.com", "user_1", "user_1", List.of(role.getAuthority()));

        when(userRepository.findByEmail(request.email())).thenReturn(Optional.of(user_1));

        assertThrows(UserAlreadyExistsException.class, () -> adminUserService.createUser(token, request));

        verify(userRepository, times(1)).findByEmail(request.email());
    }

    @Test
    @DisplayName("Test create user but role not found")
    public void testCreateUser_RoleNotFount() {
        String token = "This is a token";

        UserModificationRequest request = new UserModificationRequest("user_1@test.com", "user_1", "user_1", List.of("ROLE NOT EXISTS"));

        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());
        when(roleRepository.findByAuthority(anyString())).thenReturn(Optional.empty());

        assertThrows(RoleNotFoundException.class, () -> adminUserService.createUser(token, request));

        verify(userRepository, times(1)).findByEmail(request.email());
        verify(roleRepository, times(1)).findByAuthority(anyString());
    }

    @Test
    @DisplayName("Test update user")
    void testUpdateUser_Success() {
        String token = "token";
        UserModificationRequest request = new UserModificationRequest("user_1@test.com", "user_1_update", "user_1_update", List.of());

        User user_1 = User.builder()
                .email("user_1@test.com")
                .firstName("user_1")
                .lastName("user_1")
                .roles(List.of())
                .build();

        when(userRepository.findByEmail(request.email())).thenReturn(Optional.of(user_1));
        when(httpHeadersUtil.createHeaders(token)).thenReturn(new HttpHeaders());

        ResponseEntity<MessageResponse> response = adminUserService.updateUser(token, request);

        User savedUser = userRepository.findByEmail(request.email())
                .orElse(null);

        assertThat(response.getBody().getMessage()).isEqualTo(Messages.ENTITY_UPDATED.formatMessage(USER, request.email()));

        assertThat(savedUser).isNotNull();
        assertThat(savedUser.getFirstName()).isEqualTo(request.firstName());
        assertThat(savedUser.getLastName()).isEqualTo(request.lastName());

        verify(userRepository, times(1)).save(user_1);
        verify(httpHeadersUtil).createHeaders(token);
    }

    @Test
    @DisplayName("Test update user but user not found")
    void testUpdateUser_UserNotFound() {
        String token = "token";
        UserModificationRequest request = new UserModificationRequest("user_1@test.com", "user_1_update", "user_1_update", List.of());

        when(userRepository.findByEmail(request.email())).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class, () -> adminUserService.updateUser(token, request));

        verify(userRepository, times(1)).findByEmail(request.email());
        verify(userRepository, never()).save(any(User.class));
    }
}
