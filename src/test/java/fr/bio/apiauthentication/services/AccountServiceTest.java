package fr.bio.apiauthentication.services;

import fr.bio.apiauthentication.components.HttpHeadersUtil;
import fr.bio.apiauthentication.dto.MessageResponse;
import fr.bio.apiauthentication.dto.account.UpdateUserProfilRequest;
import fr.bio.apiauthentication.dto.admin.UserStructureResponse;
import fr.bio.apiauthentication.entities.Role;
import fr.bio.apiauthentication.entities.User;
import fr.bio.apiauthentication.enums.Messages;
import fr.bio.apiauthentication.repositories.RoleRepository;
import fr.bio.apiauthentication.repositories.UserRepository;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@DisplayName("Test Account Service")
public class AccountServiceTest {
    private static final LocalDate NOW = LocalDate.now();

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private JwtService jwtService;

    @Mock
    private HttpHeadersUtil httpHeadersUtil;

    @InjectMocks
    private AccountService accountService;

    private User user;

    private String token;

    private String email;
    private String firstName;
    private String lastName;
    private LocalDate createdAt;
    private String createdBy;
    private LocalDate modifiedAt;
    private String modifiedBy;
    private boolean enabled;

    private HttpHeaders headers;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);

        email = RandomStringUtils.randomAlphanumeric(10) + "@test.com";
        firstName = RandomStringUtils.randomAlphanumeric(20);
        lastName = RandomStringUtils.randomAlphanumeric(20);
        createdAt = NOW;
        createdBy = RandomStringUtils.randomAlphanumeric(20);
        modifiedAt = NOW;
        modifiedBy = RandomStringUtils.randomAlphanumeric(20);
        enabled = Boolean.parseBoolean(RandomStringUtils.randomNumeric(0, 1));

        Role role = Role.builder()
                .authority(RandomStringUtils.randomAlphanumeric(10).toUpperCase())
                .displayName(RandomStringUtils.randomAlphanumeric(20))
                .description(RandomStringUtils.randomAlphanumeric(20))
                .modifiedAt(NOW)
                .modifiedBy(RandomStringUtils.randomAlphanumeric(20))
                .build();
        roleRepository.save(role);

        user = User.builder()
                .email(email)
                .password(RandomStringUtils.randomAlphanumeric(30))
                .firstName(firstName)
                .lastName(lastName)
                .createdAt(createdAt)
                .createdBy(createdBy)
                .modifiedAt(modifiedAt)
                .modifiedBy(modifiedBy)
                .enabled(enabled)
                .roles(List.of(role))
                .build();
        userRepository.save(user);

        token = jwtService.generateToken(user);

        headers = httpHeadersUtil.createHeaders(token);
    }

    @Test
    @DisplayName("Test get user profile")
    public void testGetUserStructure_Success() {
        when(jwtService.getUsernameFromToken(token)).thenReturn(email);
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(httpHeadersUtil.createHeaders(token)).thenReturn(headers);

        ResponseEntity<UserStructureResponse> responseEntity = accountService.getUserStructure(token);

        assertThat(responseEntity).isNotNull();
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseEntity.getBody()).isNotNull();

        if (responseEntity.getBody() != null) {
            assertThat(responseEntity.getBody().getEmail()).isEqualTo(email);
            assertThat(responseEntity.getBody().getFirstName()).isEqualTo(firstName);
            assertThat(responseEntity.getBody().getLastName()).isEqualTo(lastName);
            assertThat(responseEntity.getBody().getCreatedAt()).isEqualTo(createdAt.toEpochDay());
            assertThat(responseEntity.getBody().getCreatedBy()).isEqualTo(createdBy);
            assertThat(responseEntity.getBody().getModifiedAt()).isEqualTo(modifiedAt.toEpochDay());
            assertThat(responseEntity.getBody().getModifiedBy()).isEqualTo(modifiedBy);
            assertThat(responseEntity.getBody().isEnabled()).isEqualTo(enabled);
        }

        verify(jwtService, times(1)).getUsernameFromToken(token);
        verify(userRepository, times(1)).findByEmail(email);
    }

    @Test
    @DisplayName("Test get user profile but user not found")
    void testGetUserProfil_UserNotFound() {
        when(jwtService.getUsernameFromToken(token)).thenReturn(email);
        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class, () -> accountService.getUserStructure(token));

        verify(jwtService, times(1)).getUsernameFromToken(token);
        verify(userRepository, times(1)).findByEmail(email);
    }

    @Test
    @DisplayName("Test update user profile")
    public void testUpdateProfile_Success() {
        when(jwtService.getUsernameFromToken(token)).thenReturn(email);
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(httpHeadersUtil.createHeaders(anyString())).thenReturn(new HttpHeaders());

        firstName = RandomStringUtils.randomAlphanumeric(20);
        lastName = RandomStringUtils.randomAlphanumeric(20);

        UpdateUserProfilRequest request = new UpdateUserProfilRequest(firstName, lastName, null);

        ResponseEntity<MessageResponse> responseEntity = accountService.updateProfile(token, request);

        assertThat(responseEntity).isNotNull();
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseEntity.getBody()).isNotNull();

        if (responseEntity.getBody() != null) {
            assertThat(responseEntity.getBody().getMessage()).isEqualTo(Messages.ACCOUNT_UPDATED.formatMessage(email));
        }

        User updatedUser = userRepository.findByEmail(email).orElse(null);

        assertThat(updatedUser).isNotNull();

        if (updatedUser != null) {
            assertThat(updatedUser.getFirstName()).isEqualTo(firstName);
            assertThat(updatedUser.getLastName()).isEqualTo(lastName);
        }

        verify(jwtService, times(1)).getUsernameFromToken(token);
        verify(userRepository, times(2)).findByEmail(email);
        verify(userRepository, times(2)).save(user);
    }

    @Test
    @DisplayName("Test update user profile but user not found")
    public void testUpdateUserProfile_NotFound() {
        when(jwtService.getUsernameFromToken(token)).thenReturn(email);
        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        firstName = RandomStringUtils.randomAlphanumeric(20);
        lastName = RandomStringUtils.randomAlphanumeric(20);

        UpdateUserProfilRequest request = new UpdateUserProfilRequest(firstName, lastName, null);

        assertThrows(UsernameNotFoundException.class, () -> accountService.updateProfile(token, request));

        verify(jwtService, times(1)).getUsernameFromToken(token);
        verify(userRepository, times(1)).findByEmail(email);
    }

    @Test
    @DisplayName("Test deactivate account")
    public void testUpdateStatus_Success() {
        when(jwtService.getUsernameFromToken(token)).thenReturn(email);
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(httpHeadersUtil.createHeaders(anyString())).thenReturn(new HttpHeaders());

        ResponseEntity<MessageResponse> responseEntity = accountService.statusAccount(token, !enabled);

        assertThat(responseEntity).isNotNull();
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseEntity.getBody()).isNotNull();

        if (responseEntity.getBody() != null) {
            if (enabled)
                assertThat(responseEntity.getBody().getMessage()).isEqualTo(Messages.ENTITY_DEACTIVATED.formatMessage("User", email));
            else
                assertThat(responseEntity.getBody().getMessage()).isEqualTo(Messages.ENTITY_ACTIVATED.formatMessage("User", email));
        }

        User savedUser = userRepository.findByEmail(email).orElse(null);

        assertThat(savedUser).isNotNull();

        if (user != null)
            assertThat(user.isEnabled()).isNotEqualTo(enabled);

        verify(jwtService, times(1)).getUsernameFromToken(token);
        verify(userRepository, times(2)).findByEmail(email);
        verify(userRepository, times(2)).save(user);
    }

    @Test
    @DisplayName("Test deactivate account but user not found")
    public void testStatusAccount_UserNotFound() {
        when(jwtService.getUsernameFromToken(token)).thenReturn("c.tronel@test.properties.com");
        when(userRepository.findByEmail("c.tronel@test.properties.com")).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class, () -> accountService.statusAccount(token, false));

        verify(jwtService, times(1)).getUsernameFromToken(token);
        verify(userRepository, times(1)).findByEmail("c.tronel@test.properties.com");
    }
}