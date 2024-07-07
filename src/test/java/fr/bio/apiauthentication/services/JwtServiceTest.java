package fr.bio.apiauthentication.services;

import fr.bio.apiauthentication.entities.Role;
import fr.bio.apiauthentication.entities.User;
import fr.bio.apiauthentication.repositories.RoleRepository;
import fr.bio.apiauthentication.repositories.UserRepository;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.userdetails.UserDetails;

import javax.crypto.SecretKey;
import java.time.LocalDate;
import java.util.*;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.when;

@DisplayName("Test JWT Service")
public class JwtServiceTest {
    private static final LocalDate NOW = LocalDate.now();

    private static final Date NOW_DATE = new Date(System.currentTimeMillis());

    private static final Random RANDOM = new Random();

    @InjectMocks
    private JwtService jwtService;

    @Mock
    private UserDetails userDetails;

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private DateUtils dateUtils;

    private User user;

    private Long expiration;

    private String secretKey;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        expiration = (long) ((1 + RANDOM.nextInt(10)) * 60 * 1000);

        Role role = Role.builder()
                .authority(RandomStringUtils.randomAlphanumeric(10).toUpperCase())
                .displayName(RandomStringUtils.randomAlphanumeric(20))
                .description(RandomStringUtils.randomAlphanumeric(20))
                .modifiedAt(NOW)
                .modifiedBy(RandomStringUtils.randomAlphanumeric(20))
                .enabled(true)
                .build();
        roleRepository.save(role);

        user = User.builder()
                .email(RandomStringUtils.randomAlphanumeric(5).toUpperCase())
                .password(RandomStringUtils.randomAlphanumeric(30))
                .firstName(RandomStringUtils.randomAlphanumeric(20))
                .lastName(RandomStringUtils.randomAlphanumeric(20))
                .createdAt(NOW)
                .createdBy(RandomStringUtils.randomAlphanumeric(20))
                .modifiedAt(NOW)
                .modifiedBy(RandomStringUtils.randomAlphanumeric(20))
                .enabled(true)
                .roles(List.of(role))
                .build();
        userRepository.save(user);

        secretKey = RandomStringUtils.randomAlphanumeric(256);
        jwtService.setSecretKey(secretKey);
        jwtService.setJwtExpiration(1000 * 60 * 60);
        jwtService.init();
    }

    @Test
    @DisplayName("Test generate token")
    void testGenerateToken() {
        when(userDetails.getUsername()).thenReturn(user.getEmail());
        when(dateUtils.getCurrentDate()).thenReturn(NOW_DATE);

        final String token = jwtService.generateToken(userDetails);

        assertThat(token).isNotNull();
        assertThat(jwtService.getUsernameFromToken(token)).isEqualTo(user.getEmail());
        assertThat(jwtService.getExpirationDateFromToken(token)).isAfter(NOW_DATE);

    }

    @Test
    @DisplayName("Test generate token")
    void testGenerateToken_Claims() {
        final Map<String, Object> claims = new HashMap<>();
        claims.put("Authorities", userDetails.getAuthorities());

        when(userDetails.getUsername()).thenReturn(user.getEmail());
        when(dateUtils.getCurrentDate()).thenReturn(NOW_DATE);

        final String token = jwtService.generateToken(claims, userDetails);

        assertThat(token).isNotNull();
        assertThat(jwtService.getUsernameFromToken(token)).isEqualTo(user.getEmail());
        assertThat(jwtService.getExpirationDateFromToken(token)).isAfter(NOW_DATE);
    }

    @Test
    @DisplayName("Test get username")
    void testGetUsernameFromToken() {
        final String token = generateJwtToken(expiration);

        when(dateUtils.getCurrentDate()).thenReturn(NOW_DATE);

        final String username = jwtService.getUsernameFromToken(token);

        assertThat(username).isEqualTo(user.getUsername());
        assertThat(jwtService.getUsernameFromToken(token)).isEqualTo(user.getEmail());
        assertThat(jwtService.getExpirationDateFromToken(token)).isAfter(NOW_DATE);
    }

    @Test
    @DisplayName("Test is valid token")
    void testValidateToken() {
        final String token = generateJwtToken(expiration);

        when(userDetails.getUsername()).thenReturn(user.getEmail());

        assertThat(jwtService.validateToken(token, userDetails)).isTrue();
    }

    private String generateJwtToken(long expiration) {
        Map<String, Object> claims = new HashMap<>();

        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        SecretKey key = Keys.hmacShaKeyFor(keyBytes);

        return Jwts.builder()
                .claims(claims)
                .subject(user.getEmail())
                .issuedAt(NOW_DATE)
                .expiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(key, Jwts.SIG.HS256)
                .compact();
    }

    private static class DateUtils {
        public Date getCurrentDate() {
            return new Date();
        }
    }
}