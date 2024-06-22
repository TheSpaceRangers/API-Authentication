package fr.bio.apiauthentication.components;

import fr.bio.apiauthentication.entities.Token;
import fr.bio.apiauthentication.enums.TokenType;
import fr.bio.apiauthentication.repositories.TokenRepository;
import fr.bio.apiauthentication.services.JwtService;
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
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.io.IOException;
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
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain filterChain;

    @Mock
    private UserDetails userDetails;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        SecurityContextHolder.clearContext();
    }

    @Test
    @DisplayName("Test filter with valid token")
    void testDoFilterInternal_withValidToken() throws ServletException, IOException {
        String token = "valid_token";
        String userEmail = "test@example.com";

        when(request.getServletPath()).thenReturn("/api/v1/test");
        when(request.getHeader("Authorization")).thenReturn("Bearer " + token);
        when(jwtService.getUsernameFromToken(token)).thenReturn(userEmail);
        when(userDetailsService.loadUserByUsername(userEmail)).thenReturn(userDetails);
        when(jwtService.validateToken(token, userDetails)).thenReturn(true);
        when(tokenRepository.findByToken(token)).thenReturn(Optional.of(
                Token.builder()
                        .token(token)
                        .type(TokenType.BEARER)
                        .build()
        ));

        jwtTokenFilter.doFilterInternal(request, response, filterChain);

        verify(filterChain, times(1)).doFilter(request, response);
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNotNull();
    }

    @Test
    @DisplayName("Test filter with invalid token")
    void testDoFilterInternal_withInvalidToken() throws ServletException, IOException {
        String token = "invalid_token";

        when(request.getServletPath()).thenReturn("/api/v1/test");
        when(request.getHeader("Authorization")).thenReturn("Bearer " + token);
        when(jwtService.getUsernameFromToken(token)).thenReturn(null);

        jwtTokenFilter.doFilterInternal(request, response, filterChain);

        verify(filterChain, times(1)).doFilter(request, response);
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
    }

    @Test
    @DisplayName("Test filter with no auth header")
    void testDoFilterInternal_withNoAuthHeader() throws ServletException, IOException {
        when(request.getServletPath()).thenReturn("/api/v1/test");
        when(request.getHeader("Authorization")).thenReturn(null);

        jwtTokenFilter.doFilterInternal(request, response, filterChain);

        verify(filterChain, times(1)).doFilter(request, response);
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
    }

    @Test
    @DisplayName("Test filter with path exclusion")
    void testDoFilterInternal_withPathExclusion() throws ServletException, IOException {
        when(request.getServletPath()).thenReturn("/api/v1/auth/login");

        jwtTokenFilter.doFilterInternal(request, response, filterChain);

        verify(filterChain, times(1)).doFilter(request, response);
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
    }
}
