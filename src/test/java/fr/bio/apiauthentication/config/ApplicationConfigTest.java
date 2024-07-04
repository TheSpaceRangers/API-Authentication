package fr.bio.apiauthentication.config;

import fr.bio.apiauthentication.entities.User;
import fr.bio.apiauthentication.enums.Messages;
import fr.bio.apiauthentication.exceptions.already_exists.UserAlreadyExistsException;
import fr.bio.apiauthentication.repositories.UserRepository;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@DisplayName("Test application configuration")
public class ApplicationConfigTest {
    @InjectMocks
    private ApplicationConfig applicationConfig;

    @Mock
    private UserRepository userRepository;

    @Mock
    private AuthenticationConfiguration authenticationConfiguration;

    private String email;
    private String password;
    private String firstName;
    private String lastName;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        email = RandomStringUtils.randomAlphanumeric(20) + "@test.com";
        password = RandomStringUtils.randomAlphanumeric(20);
        firstName = RandomStringUtils.randomAlphanumeric(20);
        lastName = RandomStringUtils.randomAlphanumeric(20);
    }

    @Test
    @DisplayName("Test bean userDetailsService")
    public void testUserDetailsService() {
        final User user = User.builder()
                .email(email)
                .password(password)
                .firstName(firstName)
                .lastName(lastName)
                .enabled(true)
                .roles(null)
                .build();

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));

        UserDetailsService userDetailsService = applicationConfig.userDetailsService();
        UserDetails userDetails = userDetailsService.loadUserByUsername(email);

        assertThat(userDetails).isNotNull();
        assertThat(userDetails.getUsername()).isEqualTo(email);
        verify(userRepository, times(1)).findByEmail(email);
    }

    @Test
    @DisplayName("Test bean userDetailsService but user not found")
    public void testUserDetailsService_UserNotFound() {
        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        UserDetailsService userDetailsService = applicationConfig.userDetailsService();

        assertThatThrownBy(() -> userDetailsService.loadUserByUsername(email))
                .isInstanceOf(UsernameNotFoundException.class)
                .hasMessageContaining(Messages.ENTITY_NOT_FOUND.formatMessage("User", email));
    }

    @Test
    @DisplayName("Test bean authenticationProvider")
    public void testAuthenticationProvider() {
        DaoAuthenticationProvider authenticationProvider = (DaoAuthenticationProvider) applicationConfig.authenticationProvider();

        assertThat(authenticationProvider).isNotNull();
    }

    @Test
    @DisplayName("Test bean authenticationManager")
    public void testAuthenticationManager() throws Exception {
        AuthenticationManager mockAuthenticationManager = mock(AuthenticationManager.class);
        when(authenticationConfiguration.getAuthenticationManager()).thenReturn(mockAuthenticationManager);

        AuthenticationManager authenticationManager = applicationConfig.authenticationManager(authenticationConfiguration);

        assertThat(authenticationManager).isNotNull();
        verify(authenticationConfiguration, times(1)).getAuthenticationManager();
    }

    @Test
    @DisplayName("Test bean auditorProvider")
    public void testAuditorProvider() {
        AuditorAware<String> auditorAware = applicationConfig.auditorProvider();

        assertThat(auditorAware).isNotNull();
        assertThat(auditorAware).isInstanceOf(AuditorAwareImplements.class);
    }

    @Test
    @DisplayName("Test bean passwordEncoder")
    public void testPasswordEncoder() {
        PasswordEncoder passwordEncoder = applicationConfig.passwordEncoder();

        assertThat(passwordEncoder).isNotNull();
        assertThat(passwordEncoder).isInstanceOf(BCryptPasswordEncoder.class);
    }
}