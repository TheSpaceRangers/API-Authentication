package fr.bio.apiauthentication.services;

import fr.bio.apiauthentication.dto.MessageResponse;
import fr.bio.apiauthentication.dto.account.UpdatePasswordRequest;
import fr.bio.apiauthentication.dto.account.UpdateUserProfilRequest;
import fr.bio.apiauthentication.dto.account.AccountTokenRequest;
import fr.bio.apiauthentication.dto.account.UserProfilResponse;
import fr.bio.apiauthentication.entities.Role;
import fr.bio.apiauthentication.entities.User;
import fr.bio.apiauthentication.repositories.RoleRepository;
import fr.bio.apiauthentication.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Collections;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@DisplayName("Test Account Service")
public class AccountServiceTest {
    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private JwtService jwtService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AccountService accountService;

    private User user;
    private String token;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);

        Role role = Role.builder()
                .roleName("USER")
                .build();
        roleRepository.save(role);

        user = User.builder()
                .firstName("Charles")
                .lastName("Tronel")
                .email("c.tronel@test.com")
                .password("1234567890")
                .roles(Collections.singleton(role))
                .build();
        userRepository.save(user);

        token = jwtService.generateToken(user);
    }

    @Test
    @DisplayName("Test get user profile")
    public void testGetUserProfile_Success() {
        AccountTokenRequest request = new AccountTokenRequest(token);

        when(jwtService.getUsernameFromToken(token)).thenReturn("c.tronel@test.com");
        when(userRepository.findByEmail("c.tronel@test.com")).thenReturn(Optional.of(user));

        ResponseEntity<UserProfilResponse> responseEntity = accountService.getUserProfile(request  );

        assertThat(responseEntity).isNotNull();
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseEntity.getBody()).isNotNull();

        assertThat(responseEntity.getBody().getFirstName()).isEqualTo("Charles");
        assertThat(responseEntity.getBody().getLastName()).isEqualTo("Tronel");
        assertThat(responseEntity.getBody().getEmail()).isEqualTo("c.tronel@test.com");
        //assertThat(responseEntity.getBody().getRoles().get(0)).isEqualTo(Collections.singleton(role.getRoleName()));

        verify(jwtService, times(1)).getUsernameFromToken(token);
        verify(userRepository, times(1)).findByEmail("c.tronel@test.com");
    }

    @Test
    @DisplayName("Test get user profile but user not found")
    void testGetUserProfil_UserNotFound() {
        AccountTokenRequest request = new AccountTokenRequest(token);

        when(jwtService.getUsernameFromToken(token)).thenReturn("c.tronel@test.com");
        when(userRepository.findByEmail("c.tronel@test.com")).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class, () -> accountService.getUserProfile(request));

        verify(jwtService, times(1)).getUsernameFromToken(token);
        verify(userRepository, times(1)).findByEmail("c.tronel@test.com");
    }

    @Test
    @DisplayName("Test update user profile")
    public void testUpdateUserProfile_Success() {
        UpdateUserProfilRequest request = new UpdateUserProfilRequest(token, "John", "Doe", "j.doe@test.com");

        when(jwtService.getUsernameFromToken(token)).thenReturn("c.tronel@test.com");
        when(userRepository.findByEmail("c.tronel@test.com")).thenReturn(Optional.of(user));

        ResponseEntity<MessageResponse> responseEntity = accountService.updateUserProfile(request);

        assertThat(responseEntity).isNotNull();
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseEntity.getBody()).isNotNull();

        assertThat(responseEntity.getBody().getMessage()).isEqualTo("");

        verify(jwtService, times(1)).getUsernameFromToken(token);
        verify(userRepository, times(1)).findByEmail("c.tronel@test.com");
        verify(userRepository, times(2)).save(user);
    }

    @Test
    @DisplayName("Test update user profile but user not found")
    public void testUpdateUserProfile_UserNotFound() {
        UpdateUserProfilRequest request = new UpdateUserProfilRequest(token, "John", "Doe", "j.doe@test.com");

        when(jwtService.getUsernameFromToken(token)).thenReturn("c.tronel@test.com");
        when(userRepository.findByEmail("c.tronel@test.com")).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class, () -> accountService.updateUserProfile(request));

        verify(jwtService, times(1)).getUsernameFromToken(token);
        verify(userRepository, times(1)).findByEmail("c.tronel@test.com");
    }

    @Test
    @DisplayName("Test update password")
    public void testUpdatePassword_Success() {
        UpdatePasswordRequest request = new UpdatePasswordRequest(token, "1234567890", "newPassword");

        when(jwtService.getUsernameFromToken(token)).thenReturn("c.tronel@test.com");
        when(userRepository.findByEmail("c.tronel@test.com")).thenReturn(Optional.of(user));
        when(userRepository.save(user)).thenReturn(user);
        when(passwordEncoder.matches(request.oldPassword(), user.getPassword())).thenReturn(true);
        when(passwordEncoder.encode(request.newPassword())).thenReturn("newPassword");

        ResponseEntity<MessageResponse> responseEntity = accountService.updatePassword(request);

        assertThat(responseEntity).isNotNull();
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseEntity.getBody()).isNotNull();

        verify(jwtService, times(1)).getUsernameFromToken(token);
        verify(userRepository, times(1)).findByEmail("c.tronel@test.com");
        verify(userRepository, times(2)).save(user);
    }

    @Test
    @DisplayName("Test update password but old password is invalid")
    public void testUpdatePassword_OldPasswordIsInvalid() {
        UpdatePasswordRequest request = new UpdatePasswordRequest(token, "invalidOldPassword", "newPassword");

        when(jwtService.getUsernameFromToken(token)).thenReturn("c.tronel@test.com");
        when(userRepository.findByEmail("c.tronel@test.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(request.oldPassword(), user.getPassword())).thenReturn(false);

        ResponseEntity<MessageResponse> responseEntity = accountService.updatePassword(request);

        assertThat(responseEntity).isNotNull();
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);

        verify(jwtService, times(1)).getUsernameFromToken(token);
        verify(userRepository, times(1)).findByEmail("c.tronel@test.com");
        verify(passwordEncoder, times(1)).matches(request.oldPassword(), user.getPassword());
    }

    @Test
    @DisplayName("Test update password but user not found")
    public void testUpdatePassword_UserNotFound() {
        UpdatePasswordRequest request = new UpdatePasswordRequest(token, "oldPassword", "newPassword");

        when(jwtService.getUsernameFromToken(token)).thenReturn("c.tronel@test.com");
        when(userRepository.findByEmail("c.tronel@test.com")).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class, () -> accountService.updatePassword(request));

        verify(jwtService, times(1)).getUsernameFromToken(token);
        verify(userRepository, times(1)).findByEmail("c.tronel@test.com");
    }
}
