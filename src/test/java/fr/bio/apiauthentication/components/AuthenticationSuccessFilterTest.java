package fr.bio.apiauthentication.components;

import fr.bio.apiauthentication.entities.LoginHistory;
import fr.bio.apiauthentication.entities.User;
import fr.bio.apiauthentication.repositories.LoginHistoryRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.io.IOException;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@DisplayName("Test authentication success filter")
public class AuthenticationSuccessFilterTest {
    @Mock
    private LoginHistoryRepository loginHistoryRepository;

    @InjectMocks
    private AuthenticationSuccessFilter authenticationSuccessFilter;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain filterChain;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("Test filter with authenticated user")
    public void testDoFilterInternal_AuthenticatedUser() throws IOException, ServletException {
        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);

        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        User user = User.builder()
                .email("c.tronel@test.properties.com")
                .password("password")
                .firstName("firstName")
                .lastName("lastName")
                .enabled(true)
                .roles(null)
                .build();
        when(authentication.getPrincipal()).thenReturn(user);
        when(authentication.isAuthenticated()).thenReturn(true);

        authenticationSuccessFilter.doFilterInternal(request, response, filterChain);

        verify(loginHistoryRepository, times(1)).save(any(LoginHistory.class));
        verify(filterChain, times(1)).doFilter(request, response);
    }

    @Test
    @DisplayName("Test filter with no authenticated user")
    public void testDoFilterInternal_NotAuthenticatedUser() throws IOException, ServletException {
        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);

        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        when(authentication.isAuthenticated()).thenReturn(false);

        authenticationSuccessFilter.doFilterInternal(request, response, filterChain);

        verify(loginHistoryRepository, times(0)).save(any(LoginHistory.class));
        verify(filterChain, times(1)).doFilter(request, response);
    }
}
