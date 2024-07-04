package fr.bio.apiauthentication.config;

import fr.bio.apiauthentication.entities.User;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.*;

@DisplayName("Test auditor aware implementation")
public class AuditorAwareImplementsTest {
    @InjectMocks
    private AuditorAwareImplements auditorAware;

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
    @DisplayName("Test get current auditor with authenticated User")
    public void testGetCurrentAuditor_withAuthenticatedUser() {
        final User user = User.builder()
                .email(email)
                .password(password)
                .firstName(firstName)
                .lastName(lastName)
                .enabled(true)
                .roles(null)
                .build();

        final Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(user);

        final SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);

        SecurityContextHolder.setContext(securityContext);

        Optional<String> currentAuditor = auditorAware.getCurrentAuditor();
        assertThat(currentAuditor.get()).isEqualTo(user.getFirstName().charAt(0) + "." + user.getLastName());
    }

    @Test
    @DisplayName("Test get current auditor with null authentication")
    public void testGetCurrentAuditor_withNullAuthentication() {
        final SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(null);

        SecurityContextHolder.setContext(securityContext);

        Optional<String> currentAuditor = auditorAware.getCurrentAuditor();
        assertThat(currentAuditor.get()).isEqualTo("system");
    }

    @Test
    public void testGetCurrentAuditor_withDifferentPrincipal() {
        final String principal = RandomStringUtils.randomAlphanumeric(20);

        final UserDetails userDetails = mock(UserDetails.class);
        when(userDetails.getUsername()).thenReturn(principal);

        final Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(userDetails);

        final SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);

        SecurityContextHolder.setContext(securityContext);

        Optional<String> currentAuditor = auditorAware.getCurrentAuditor();
        assertThat(principal).isEqualTo(currentAuditor.get());
    }
}