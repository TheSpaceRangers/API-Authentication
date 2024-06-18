package fr.bio.apiauthentication.services;

import fr.bio.apiauthentication.dto.AuthenticationResponse;
import fr.bio.apiauthentication.dto.CreateUserRequest;
import fr.bio.apiauthentication.entities.Role;
import fr.bio.apiauthentication.entities.User;
import fr.bio.apiauthentication.exceptions.RoleNotFoundException;
import fr.bio.apiauthentication.repositories.RoleRepository;
import fr.bio.apiauthentication.repositories.UserRepository;

import jakarta.transaction.Transactional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@DisplayName("Test Authentication Service")
public class AuthenticationServiceTest {
    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthenticationService authenticationService;

    private CreateUserRequest request;
    private User user;
    private Role role;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);

        request = new CreateUserRequest(
                "Charles",
                "TRONEL",
                "c.tronel@test.com",
                "1234567890"
        );

        user = User.builder()
                .firstName("Charles")
                .lastName("Tronel")
                .email("c.tronel@test.com")
                .password("1234567890")
                .build();

        role = Role.builder()
                .roleName("USER")
                .build();
        roleRepository.save(role);
    }

    @Test
    @Transactional
    public void testRegisterUser() {
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(user);
        when(roleRepository.findByRoleName("USER")).thenReturn(Optional.of(role));

        ResponseEntity<AuthenticationResponse> response = authenticationService.register(request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().getMessage()).isEqualTo("L'utilisateur c.tronel@test.com a bien été créé !");

        verify(passwordEncoder, times(1)).encode("1234567890");
        verify(userRepository, times(1)).save(any(User.class));
        verify(roleRepository, times(1)).findByRoleName("USER");
    }

    @Test
    public void testRegisterUserRoleNotExists() {
        when(roleRepository.findByRoleName("USER")).thenReturn(Optional.empty());

        RoleNotFoundException exception = assertThrows(
                RoleNotFoundException.class,
                () -> authenticationService.register(request)
        );

        assertThat(exception.getMessage()).isEqualTo("Role 'USER' not found");

        verify(roleRepository, times(1)).findByRoleName("USER");
        verify(passwordEncoder, never()).encode(anyString());
        verify(userRepository, never()).save(any(User.class));
    }
}