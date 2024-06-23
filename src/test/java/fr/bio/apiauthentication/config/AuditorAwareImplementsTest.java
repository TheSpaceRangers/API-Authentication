package fr.bio.apiauthentication.config;

import fr.bio.apiauthentication.entities.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockedStatic;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.*;

@DisplayName("Test auditor aware implementation")
public class AuditorAwareImplementsTest {
    @InjectMocks
    private AuditorAwareImplements auditorAware;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("Test get current auditor when authenticated")
    public void testGetCurrentAuditorWhenAuthenticated() {
        User user = User.builder()
                .email("test@test.com")
                .password("password")
                .firstName("John")
                .lastName("Doe")
                .enabled(true)
                .roles(null)
                .build();
        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);

        when(authentication.getPrincipal()).thenReturn(user);
        when(securityContext.getAuthentication()).thenReturn(authentication);

        try (MockedStatic<SecurityContextHolder> mockedStatic = mockStatic(SecurityContextHolder.class)) {
            mockedStatic.when(SecurityContextHolder::getContext).thenReturn(securityContext);

            Optional<String> auditor = auditorAware.getCurrentAuditor();

            assertThat(auditor).isPresent();
            assertThat(auditor.get()).isEqualTo("J.Doe");
        }
    }

    @Test
    @DisplayName("Test get current auditor when not authenticated")
    public void testGetCurrentAuditorWhenNotAuthenticated() {
        SecurityContext securityContext = mock(SecurityContext.class);

        when(securityContext.getAuthentication()).thenReturn(null);

        try (MockedStatic<SecurityContextHolder> mockedStatic = mockStatic(SecurityContextHolder.class)) {
            mockedStatic.when(SecurityContextHolder::getContext).thenReturn(securityContext);

            Optional<String> auditor = auditorAware.getCurrentAuditor();

            assertThat(auditor).isPresent();
            assertThat(auditor.get()).isEqualTo("system");
        }
    }

    @Test
    @DisplayName("Test get current auditor when principal not user details")
    public void testGetCurrentAuditorWhenPrincipalNotUserDetails() {
        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);

        when(authentication.getPrincipal()).thenReturn("principal");
        when(securityContext.getAuthentication()).thenReturn(authentication);

        try (MockedStatic<SecurityContextHolder> mockedStatic = mockStatic(SecurityContextHolder.class)) {
            mockedStatic.when(SecurityContextHolder::getContext).thenReturn(securityContext);

            Optional<String> auditor = auditorAware.getCurrentAuditor();

            assertThat(auditor).isPresent();
            assertThat(auditor.get()).isEqualTo("system");
        }
    }
}
