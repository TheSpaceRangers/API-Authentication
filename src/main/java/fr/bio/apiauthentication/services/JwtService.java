package fr.bio.apiauthentication.services;

import fr.bio.apiauthentication.entities.Token;
import fr.bio.apiauthentication.entities.User;
import fr.bio.apiauthentication.enums.Messages;
import fr.bio.apiauthentication.enums.TokenType;
import fr.bio.apiauthentication.repositories.TokenRepository;
import fr.bio.apiauthentication.repositories.UserRepository;
import fr.bio.apiauthentication.services.interfaces.IJwtService;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

import jakarta.annotation.PostConstruct;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

@Service
@Data
@RequiredArgsConstructor
@Slf4j
public class JwtService implements IJwtService {
    private static final String USER = "User";

    private final UserRepository userRepository;
    private final TokenRepository tokenRepository;

    @Value("${application.security.jwt.secret-key}")
    private String secretKey;

    @Value("${application.security.jwt.expiration}")
    private long jwtExpiration;

    private SecretKey key;

    @PostConstruct
    public void init() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        key = Keys.hmacShaKeyFor(keyBytes);
    }

    private String buildToken (
            Map<String, Object> claims,
            UserDetails userDetails,
            long expiration
    ) {
        return Jwts.builder()
                .claims(claims)
                .subject(userDetails.getUsername())
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(key, Jwts.SIG.HS256)
                .compact();
    }

    private  <T> T extractClaim(
            String token,
            Function<Claims, T> claimsResolver
    ) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(
            String token
    ) {
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    @Override
    public String generateToken(
            UserDetails userDetails
    ) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("Authorities", userDetails.getAuthorities());

        return generateToken(claims, userDetails);
    }

    @Override
    public String generateToken(
            Map<String, Object> extraClaims,
            UserDetails userDetails
    ) {
        return buildToken(extraClaims, userDetails, jwtExpiration);
    }

    @Override
    public boolean validateToken(
            String token,
            UserDetails userDetails
    ) {
        final String username = getUsernameFromToken(token);

        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    @Override
    public String getUsernameFromToken(
            String token
    ) {
        return extractClaim(token, Claims::getSubject);
    }

    @Override
    public Date getExpirationDateFromToken(
            String token
    ) {
        return extractClaim(token, Claims::getExpiration);
    }

    @Override
    public boolean isTokenExpired(
            String token
    ) {
        return getExpirationDateFromToken(token).before(new Date());
    }

    @Override
    public void saveUserToken(
            UserDetails userDetails,
            String strToken,
            TokenType type
    ) {
        User user = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new UsernameNotFoundException(Messages.ENTITY_NOT_FOUND.formatMessage(USER, userDetails.getUsername())));

        Token token = Token.builder()
                .token(strToken)
                .type(type)
                .user(user)
                .build();
        tokenRepository.save(token);
    }

    @Override
    public void revokeAllUserTokens(
            UserDetails userDetails,
            TokenType type
    ) {
        User user = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new UsernameNotFoundException(Messages.ENTITY_NOT_FOUND.formatMessage(USER, userDetails.getUsername())));

        List<Token> tokens = tokenRepository.findByUserAndTypeAndExpiredFalseAndRevokedFalse(user, type);

        tokens.forEach(token -> {
            token.setExpired(true);
            token.setRevoked(true);
        });

        tokenRepository.saveAll(tokens);
    }
}