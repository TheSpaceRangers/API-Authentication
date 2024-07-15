package fr.bio.apiauthentication.services;

import fr.bio.apiauthentication.components.HttpHeadersUtil;
import fr.bio.apiauthentication.dto.MessageResponse;
import fr.bio.apiauthentication.dto.account.UpdateUserProfilRequest;
import fr.bio.apiauthentication.dto.admin.UserStructureResponse;
import fr.bio.apiauthentication.entities.Role;
import fr.bio.apiauthentication.entities.User;
import fr.bio.apiauthentication.enums.Messages;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@DisplayName("Test Account Service")
public class AccountServiceTest {
    private static final LocalDate NOW = LocalDate.now();

    @Mock
    private UserRepository userRepository;

    @Mock
    private JwtService jwtService;

    @Mock
    private HttpHeadersUtil httpHeadersUtil;

    @InjectMocks
    private AccountService accountService;

    private String token;

    private HttpHeaders headers;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("Test get user structure")
    public void testGetUserStructure_Success() {
        final User savedUser = generateUser();

        token = jwtService.generateToken(savedUser);
        headers = httpHeadersUtil.createHeaders(token);

        when(jwtService.getUsernameFromToken(token)).thenReturn(savedUser.getEmail());
        when(userRepository.findByEmail(savedUser.getEmail())).thenReturn(Optional.of(savedUser));
        when(httpHeadersUtil.createHeaders(token)).thenReturn(headers);

        final ResponseEntity<UserStructureResponse> responseEntity = accountService.getUserStructure(token);

        assertThat(responseEntity).isNotNull();
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseEntity.getBody()).isNotNull();

        if (responseEntity.getBody() != null) {
            assertThat(responseEntity.getBody().getEmail()).isEqualTo(savedUser.getEmail());
            assertThat(responseEntity.getBody().getFirstName()).isEqualTo(savedUser.getFirstName());
            assertThat(responseEntity.getBody().getLastName()).isEqualTo(savedUser.getLastName());
            assertThat(responseEntity.getBody().getCreatedAt()).isEqualTo(savedUser.getCreatedAt().toEpochDay());
            assertThat(responseEntity.getBody().getCreatedBy()).isEqualTo(savedUser.getCreatedBy());
            assertThat(responseEntity.getBody().getModifiedAt()).isEqualTo(savedUser.getModifiedAt().toEpochDay());
            assertThat(responseEntity.getBody().getModifiedBy()).isEqualTo(savedUser.getModifiedBy());
            assertThat(responseEntity.getBody().isEnabled()).isEqualTo(savedUser.isEnabled());

            final List<String> expectedRoles = savedUser.getRoles().stream()
                    .map(role -> role.getAuthority() + " : " + role.getDisplayName())
                    .toList();
            assertThat(responseEntity.getBody().getRoles()).isEqualTo(expectedRoles);
        }

        verify(jwtService, times(1)).getUsernameFromToken(token);
        verify(userRepository, times(1)).findByEmail(savedUser.getEmail());
    }

    @Test
    @DisplayName("Test get user structure but user not found")
    void testGetUserStructure_UserNotFound() {
        final User savedUser = generateUser();

        token = jwtService.generateToken(savedUser);

        when(jwtService.getUsernameFromToken(token)).thenReturn(savedUser.getEmail());
        when(userRepository.findByEmail(savedUser.getEmail())).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class, () -> accountService.getUserStructure(token));

        verify(jwtService, times(1)).getUsernameFromToken(token);
        verify(userRepository, times(1)).findByEmail(savedUser.getEmail());
    }

    @Test
    @DisplayName("Test modify user")
    public void testModify_Success() {
        final User savedUser = generateUser();

        final String actualEmail = savedUser.getEmail();

        token = jwtService.generateToken(savedUser);
        headers = httpHeadersUtil.createHeaders(token);

        final String newEmail = RandomStringUtils.randomAlphanumeric(20) + "@test.com";
        final String firstName = RandomStringUtils.randomAlphanumeric(20);
        final String lastName = RandomStringUtils.randomAlphanumeric(20);

        final UpdateUserProfilRequest request = new UpdateUserProfilRequest(firstName, lastName, newEmail);

        when(jwtService.getUsernameFromToken(token)).thenReturn(actualEmail);
        when(userRepository.findByEmail(actualEmail)).thenReturn(Optional.of(savedUser));
        when(httpHeadersUtil.createHeaders(token)).thenReturn(headers);

        final ResponseEntity<MessageResponse> responseEntity = accountService.modify(token, request);

        assertThat(responseEntity).isNotNull();
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseEntity.getBody()).isNotNull();

        if (responseEntity.getBody() != null)
            assertThat(responseEntity.getBody().getMessage()).isEqualTo(Messages.ACCOUNT_UPDATED.formatMessage(actualEmail));

        final ArgumentCaptor<User> roleCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository, times(1)).save(roleCaptor.capture());
        final User updatedUser = roleCaptor.getValue();

        assertThat(updatedUser).isNotNull();

        if (updatedUser != null) {
            assertThat(updatedUser.getFirstName()).isEqualTo(firstName);
            assertThat(updatedUser.getLastName()).isEqualTo(lastName);
            assertThat(updatedUser.getEmail()).isEqualTo(newEmail);
        }

        verify(jwtService, times(1)).getUsernameFromToken(token);
        verify(userRepository, times(1)).findByEmail(actualEmail);
    }

    @Test
    @DisplayName("Test update profile with no changes")
    void testModify_NoChanges() {
        final User savedUser = generateUser();

        token = jwtService.generateToken(savedUser);
        headers = httpHeadersUtil.createHeaders(token);

        final UpdateUserProfilRequest request = new UpdateUserProfilRequest(savedUser.getFirstName(), savedUser.getLastName(), savedUser.getEmail());

        when(jwtService.getUsernameFromToken(token)).thenReturn(savedUser.getEmail());
        when(userRepository.findByEmail(savedUser.getEmail())).thenReturn(Optional.of(savedUser));
        when(httpHeadersUtil.createHeaders(token)).thenReturn(headers);

        final ResponseEntity<MessageResponse> response = accountService.modify(token, request);

        assertThat(response).isNotNull();
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();

        if (response.getBody() != null)
            assertThat(response.getBody().getMessage()).isEqualTo(Messages.ENTITY_NO_MODIFIED.formatMessage("User", savedUser.getEmail()));

        verify(jwtService, times(1)).getUsernameFromToken(token);
        verify(userRepository, times(1)).findByEmail(savedUser.getEmail());
        verify(userRepository, never()).save(savedUser);
    }

    @Test
    @DisplayName("Test update profile with partial changes")
    void testModify_PartialChanges() {
        final User savedUser = generateUser();

        token = jwtService.generateToken(savedUser);
        headers = httpHeadersUtil.createHeaders(token);

        final String firstName = RandomStringUtils.randomAlphanumeric(20);

        final UpdateUserProfilRequest request = new UpdateUserProfilRequest(firstName, "", savedUser.getEmail());

        when(jwtService.getUsernameFromToken(token)).thenReturn(savedUser.getEmail());
        when(userRepository.findByEmail(savedUser.getEmail())).thenReturn(Optional.of(savedUser));
        when(httpHeadersUtil.createHeaders(token)).thenReturn(headers);

        final ResponseEntity<MessageResponse> response = accountService.modify(token, request);

        final ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userCaptor.capture());
        final User updatedUser = userCaptor.getValue();

        assertThat(updatedUser.getEmail()).isEqualTo(savedUser.getEmail());
        assertThat(updatedUser.getFirstName()).isEqualTo(firstName);
        assertThat(updatedUser.getLastName()).isEqualTo(savedUser.getLastName());

        if (response.getBody() != null)
            assertThat(response.getBody().getMessage()).isEqualTo(Messages.ACCOUNT_UPDATED.formatMessage(savedUser.getEmail()));

        verify(jwtService, times(1)).getUsernameFromToken(token);
        verify(userRepository, times(1)).findByEmail(savedUser.getEmail());
    }

    @Test
    @DisplayName("Test update user profile but user not found")
    public void testUpdateUserProfile_NotFound() {
        final User savedUser = generateUser();

        token = jwtService.generateToken(savedUser);

        when(jwtService.getUsernameFromToken(token)).thenReturn(savedUser.getEmail());
        when(userRepository.findByEmail(savedUser.getEmail())).thenReturn(Optional.empty());

        final UpdateUserProfilRequest request = new UpdateUserProfilRequest(savedUser.getFirstName(), savedUser.getLastName(), savedUser.getEmail());

        assertThrows(UsernameNotFoundException.class, () -> accountService.modify(token, request));

        verify(jwtService, times(1)).getUsernameFromToken(token);
        verify(userRepository, times(1)).findByEmail(savedUser.getEmail());
        verify(userRepository, never()).save(savedUser);
    }

    @Test
    @DisplayName("Test activate account")
    public void testModifyStatus_Activate() {
        final User savedUser = generateUser();
        savedUser.setEnabled(false);

        token = jwtService.generateToken(savedUser);
        headers = httpHeadersUtil.createHeaders(token);

        when(jwtService.getUsernameFromToken(token)).thenReturn(savedUser.getEmail());
        when(userRepository.findByEmail(savedUser.getEmail())).thenReturn(Optional.of(savedUser));
        when(httpHeadersUtil.createHeaders(anyString())).thenReturn(headers);

        final ResponseEntity<MessageResponse> responseEntity = accountService.modifyStatus(token, true);

        assertThat(responseEntity).isNotNull();
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseEntity.getBody()).isNotNull();

        if (responseEntity.getBody() != null)
            assertThat(responseEntity.getBody().getMessage()).isEqualTo(Messages.ENTITY_ACTIVATED.formatMessage("User", savedUser.getEmail()));

        final ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userCaptor.capture());
        final User updatedUser = userCaptor.getValue();

        assertThat(updatedUser).isNotNull();
        assertThat(updatedUser.isEnabled()).isTrue();

        verify(jwtService, times(1)).getUsernameFromToken(token);
        verify(userRepository, times(1)).findByEmail(savedUser.getEmail());
    }

    @Test
    @DisplayName("Test deactivate account")
    public void testModifyStatus_Deactivate() {
        final User savedUser = generateUser();

        token = jwtService.generateToken(savedUser);
        headers = httpHeadersUtil.createHeaders(token);

        when(jwtService.getUsernameFromToken(token)).thenReturn(savedUser.getEmail());
        when(userRepository.findByEmail(savedUser.getEmail())).thenReturn(Optional.of(savedUser));
        when(httpHeadersUtil.createHeaders(anyString())).thenReturn(headers);

        final ResponseEntity<MessageResponse> responseEntity = accountService.modifyStatus(token, false);

        assertThat(responseEntity).isNotNull();
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseEntity.getBody()).isNotNull();

        if (responseEntity.getBody() != null)
            assertThat(responseEntity.getBody().getMessage()).isEqualTo(Messages.ENTITY_DEACTIVATED.formatMessage("User", savedUser.getEmail()));

        final ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userCaptor.capture());
        final User updatedUser = userCaptor.getValue();

        assertThat(updatedUser).isNotNull();
        assertThat(updatedUser.isEnabled()).isFalse();

        verify(jwtService, times(1)).getUsernameFromToken(token);
        verify(userRepository, times(1)).findByEmail(savedUser.getEmail());
    }

    @Test
    @DisplayName("Test deactivate account but user not found")
    public void testModifyStatus_UserNotFound() {
        final User savedUser = generateUser();

        token = jwtService.generateToken(savedUser);

        when(jwtService.getUsernameFromToken(token)).thenReturn(savedUser.getEmail());
        when(userRepository.findByEmail(savedUser.getEmail())).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class, () -> accountService.modifyStatus(token, false));

        verify(jwtService, times(1)).getUsernameFromToken(token);
        verify(userRepository, times(1)).findByEmail(savedUser.getEmail());
    }

    private User generateUser() {
        return User.builder()
                .idUser((long) (Math.random() * 1000))
                .email(RandomStringUtils.randomAlphanumeric(10) + "@test.com")
                .password(RandomStringUtils.randomAlphanumeric(30))
                .firstName(RandomStringUtils.randomAlphanumeric(5))
                .lastName(RandomStringUtils.randomAlphanumeric(7))
                .createdAt(NOW)
                .createdBy(RandomStringUtils.randomAlphanumeric(10))
                .modifiedAt(NOW)
                .modifiedBy(RandomStringUtils.randomAlphanumeric(10))
                .enabled(true)
                .roles(generateRoles())
                .build();
    }

    private List<Role> generateRoles() {
        return IntStream.range(0, 5)
                .mapToObj(role -> Role.builder()
                        .idRole((long) (Math.random() * 1000))
                        .authority(RandomStringUtils.randomAlphanumeric(10))
                        .displayName(RandomStringUtils.randomAlphanumeric(10))
                        .description(RandomStringUtils.randomAlphanumeric(20))
                        .build())
                .toList();
    }
}