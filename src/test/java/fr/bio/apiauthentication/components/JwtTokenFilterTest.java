package fr.bio.apiauthentication.components;

import fr.bio.apiauthentication.entities.Role;
import fr.bio.apiauthentication.entities.Token;
import fr.bio.apiauthentication.entities.User;
import fr.bio.apiauthentication.repositories.TokenRepository;
import fr.bio.apiauthentication.services.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.*;

@DisplayName("Test jwt token filter")
public class JwtTokenFilterTest {
    @InjectMocks
    private JwtTokenFilter jwtTokenFilter;

    @Mock
    private JwtService jwtService;

    @Mock
    private UserDetailsService userDetailsService;

    @Mock
    private TokenRepository tokenRepository;

    @Mock
    private FilterChain filterChain;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        SecurityContextHolder.clearContext();
    }

    @Test
    @DisplayName("Test filter with auth path")
    public void testDoFilterInternal_authPath_shouldNotFilter() throws ServletException, IOException {
        final MockHttpServletRequest request = new MockHttpServletRequest();
        request.setServletPath("/api/v1/auth/login");

        final MockHttpServletResponse response = new MockHttpServletResponse();

        jwtTokenFilter.doFilterInternal(request, response, filterChain);

        verify(filterChain, times(1)).doFilter(request, response);
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
    }

    @Test
    @DisplayName("Test filter with no auth header")
    public void testDoFilterInternal_noAuthHeader_shouldNotAuthenticate() throws ServletException, IOException {
        final MockHttpServletRequest request = new MockHttpServletRequest();
        request.setServletPath("/api/v1/test");

        final MockHttpServletResponse response = new MockHttpServletResponse();

        jwtTokenFilter.doFilterInternal(request, response, filterChain);

        verify(filterChain, times(1)).doFilter(request, response);
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
    }

    @Test
    @DisplayName("Test filter with invalid auth header")
    public void testDoFilterInternal_invalidAuthHeader_shouldNotAuthenticate() throws ServletException, IOException {
        final MockHttpServletRequest request = new MockHttpServletRequest();
        request.setServletPath("/api/v1/test");
        request.addHeader("Authorization", "InvalidToken");

        final MockHttpServletResponse response = new MockHttpServletResponse();

        jwtTokenFilter.doFilterInternal(request, response, filterChain);

        verify(filterChain, times(1)).doFilter(request, response);
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
    }

    @Test
    @DisplayName("Test filter with valid token")
    public void testDoFilterInternal_validToken_shouldAuthenticate() throws ServletException, IOException {
        final MockHttpServletRequest request = new MockHttpServletRequest();
        request.setServletPath("/api/v1/test");
        request.addHeader("Authorization", "Bearer validToken");

        final MockHttpServletResponse response = new MockHttpServletResponse();

        final User user = User.builder()
                .email("test@test.com")
                .password("password")
                .roles(List.of(Role.builder().authority("USER").build()))
                .build();

        final Token token = Token.builder()
                .token("validToken")
                .user(user)
                .revoked(false)
                .expired(false)
                .build();

        when(jwtService.getUsernameFromToken("validToken")).thenReturn("test@test.com");
        when(userDetailsService.loadUserByUsername("test@test.com")).thenReturn(user);
        when(jwtService.validateToken("validToken", user)).thenReturn(true);
        when(tokenRepository.findByToken("validToken")).thenReturn(Optional.of(token));

        jwtTokenFilter.doFilterInternal(request, response, filterChain);

        verify(filterChain, times(1)).doFilter(request, response);
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNotNull();
        assertThat(SecurityContextHolder.getContext().getAuthentication().getPrincipal()).isEqualTo(user);
    }

    @Test
    @DisplayName("Test filter with expired token")
    public void testDoFilterInternal_expiredToken_shouldNotAuthenticate() throws ServletException, IOException {
        final MockHttpServletRequest request = new MockHttpServletRequest();
        request.setServletPath("/api/v1/test");
        request.addHeader("Authorization", "Bearer expiredToken");

        final MockHttpServletResponse response = new MockHttpServletResponse();

        final User user = User.builder()
                .email("test@test.com")
                .password("password")
                .roles(List.of(Role.builder().authority("USER").build()))
                .build();

        final Token token = Token.builder()
                .token("expiredToken")
                .user(user)
                .revoked(false)
                .expired(true)
                .build();

        when(jwtService.getUsernameFromToken("expiredToken")).thenReturn("test@test.com");
        when(userDetailsService.loadUserByUsername("test@test.com")).thenReturn(user);
        when(jwtService.validateToken("expiredToken", user)).thenReturn(false);
        when(tokenRepository.findByToken("expiredToken")).thenReturn(Optional.of(token));

        jwtTokenFilter.doFilterInternal(request, response, filterChain);

        verify(filterChain, times(1)).doFilter(request, response);
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
    }

    @Test
    @DisplayName("Test filter with revoked token")
    public void testDoFilterInternal_revokedToken_shouldNotAuthenticate() throws ServletException, IOException {
        final MockHttpServletRequest request = new MockHttpServletRequest();
        request.setServletPath("/api/v1/test");
        request.addHeader("Authorization", "Bearer revokedToken");

        final MockHttpServletResponse response = new MockHttpServletResponse();

        final User user = User.builder()
                .email("test@test.com")
                .password("password")
                .roles(List.of(Role.builder().authority("USER").build()))
                .build();

        final Token token = Token.builder()
                .token("revokedToken")
                .user(user)
                .revoked(true)
                .expired(false)
                .build();

        when(jwtService.getUsernameFromToken("revokedToken")).thenReturn("test@test.com");
        when(userDetailsService.loadUserByUsername("test@test.com")).thenReturn(user);
        when(jwtService.validateToken("revokedToken", user)).thenReturn(false);
        when(tokenRepository.findByToken("revokedToken")).thenReturn(Optional.of(token));

        jwtTokenFilter.doFilterInternal(request, response, filterChain);

        verify(filterChain, times(1)).doFilter(request, response);
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
    }
}