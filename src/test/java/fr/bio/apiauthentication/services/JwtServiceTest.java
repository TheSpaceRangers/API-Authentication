package fr.bio.apiauthentication.services;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.userdetails.UserDetails;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.when;

@DisplayName("Test JWT Service")
public class JwtServiceTest {
    @InjectMocks
    private JwtService jwtService;

    @Mock
    private UserDetails userDetails;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        jwtService.setSecretKey("sMcZfQWkvZ7cjmvdxERDyxElXP+LBE0NfQD4V1eyJuQrJ4DsUJLsJXXHis+spgyNV3+gkLT0o8SS3HU3rDucbFeH1BB10jzqlj1jmYiwNEgpnmV/YYsm8rbRYRB+VJ6tL/01M2UhrHqQCiGmQzSbLUKiXpDiFk6G0ihCL0cCsTme82k59n2nR/95rBKnDtcur");
        jwtService.setJwtExpiration(1000 * 60 * 60);
        jwtService.setRefreshExpiration(1000 * 60 * 60 * 24 * 7);
        jwtService.init();
    }

    @Test
    @DisplayName("Test generate token")
    void testGenerateToken() {
        when(userDetails.getUsername()).thenReturn("testUser");
        when(userDetails.getAuthorities()).thenReturn(List.of());

        String token = jwtService.generateToken(userDetails);

        assertThat(token).isNotNull();
    }

    @Test
    @DisplayName("Test generate refresh token")
    void testGenerateRefreshToken() {
        when(userDetails.getUsername()).thenReturn("testUser");

        String refreshToken = jwtService.generateRefreshToken(userDetails);

        assertThat(refreshToken).isNotNull();
    }

    @Test
    @DisplayName("Test get username")
    void testGetUsernameFromToken() {
        String token = createTestToken(3600000);

        String username = jwtService.getUsernameFromToken(token);

        assertThat(username).isEqualTo("testUser");
    }

    @Test
    @DisplayName("Test get expiration date")
    void testGetExpirationDateFromToken() {
        String token = createTestToken(3600000);

        Date expirationDate = jwtService.getExpirationDateFromToken(token);

        assertThat(expirationDate).isNotNull();
        assertThat(expirationDate.after(new Date())).isTrue();
    }

    @Test
    @DisplayName("Test is valid token")
    void testValidateToken() {
        when(userDetails.getUsername()).thenReturn("testUser");
        String token = createTestToken(3600000);

        boolean isValid = jwtService.validateToken(token, userDetails);

        assertThat(isValid).isTrue();
    }

    private String createTestToken(long expiration) {
        Map<String, Object> claims = new HashMap<>();

        byte[] keyBytes = Decoders.BASE64.decode("sMcZfQWkvZ7cjmvdxERDyxElXP+LBE0NfQD4V1eyJuQrJ4DsUJLsJXXHis+spgyNV3+gkLT0o8SS3HU3rDucbFeH1BB10jzqlj1jmYiwNEgpnmV/YYsm8rbRYRB+VJ6tL/01M2UhrHqQCiGmQzSbLUKiXpDiFk6G0ihCL0cCsTme82k59n2nR/95rBKnDtcur");
        SecretKey key = Keys.hmacShaKeyFor(keyBytes);

        return Jwts.builder()
                .claims(claims)
                .subject("testUser")
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(key, Jwts.SIG.HS256)
                .compact();
    }
}
