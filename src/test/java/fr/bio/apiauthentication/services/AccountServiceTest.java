package fr.bio.apiauthentication.services;

import fr.bio.apiauthentication.dto.account.UserProfilRequest;
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

    @InjectMocks
    private AccountService accountService;

    private User user;
    private Role role;
    private String token;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);

        role = Role.builder()
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
    public void testGetUserProfile_Success() {
        UserProfilRequest request = new UserProfilRequest(token);

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
    void testGetUserProfil_UserNotFound() {
        UserProfilRequest request = new UserProfilRequest(token);

        when(jwtService.getUsernameFromToken(token)).thenReturn("c.tronel@test.com");
        when(userRepository.findByEmail("c.tronel@test.com")).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class, () -> accountService.getUserProfile(request));

        verify(jwtService, times(1)).getUsernameFromToken(token);
        verify(userRepository, times(1)).findByEmail("c.tronel@test.com");
    }
}
