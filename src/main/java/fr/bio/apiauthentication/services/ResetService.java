package fr.bio.apiauthentication.services;

import fr.bio.apiauthentication.components.HttpHeadersUtil;
import fr.bio.apiauthentication.dto.MessageResponse;
import fr.bio.apiauthentication.dto.reset.ResetPasswordRequest;
import fr.bio.apiauthentication.dto.reset.SendResetEmailRequest;
import fr.bio.apiauthentication.entities.Token;
import fr.bio.apiauthentication.entities.User;
import fr.bio.apiauthentication.enums.Messages;
import fr.bio.apiauthentication.enums.TokenType;
import fr.bio.apiauthentication.exceptions.TokenExpiredException;
import fr.bio.apiauthentication.exceptions.invalid.InvalidTokenException;
import fr.bio.apiauthentication.repositories.TokenRepository;
import fr.bio.apiauthentication.repositories.UserRepository;
import fr.bio.apiauthentication.services.interfaces.IEmailService;
import fr.bio.apiauthentication.services.interfaces.IJwtService;
import fr.bio.apiauthentication.services.interfaces.IResetService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ResetService implements IResetService {
    private static final TokenType PASSWORD_RESET = TokenType.PASSWORD_RESET;

    private static final String USER = "User";

    private final UserRepository userRepository;
    private final TokenRepository tokenRepository;

    private final IJwtService jwtService;
    private final IEmailService emailService;

    private final HttpHeadersUtil httpHeadersUtil;

    private final UserDetailsService userDetailsService;
    private final PasswordEncoder passwordEncoder;

    @Override
    public ResponseEntity<MessageResponse> passwordResetEmail(
            String token,
            SendResetEmailRequest request
    ) {
        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new UsernameNotFoundException(Messages.ENTITY_NOT_FOUND.formatMessage(USER, request.email())));
        UserDetails userDetails = userDetailsService.loadUserByUsername(user.getEmail());

        final String resetToken = jwtService.generateToken(userDetails);
        jwtService.revokeAllUserTokens(userDetails, PASSWORD_RESET);
        jwtService.saveUserToken(userDetails, resetToken, PASSWORD_RESET);

        emailService.sendPasswordResetEmail(
                user.getEmail(),
                resetToken
        );

        return ResponseEntity.ok()
                .headers(httpHeadersUtil.createHeaders(token))
                .body(MessageResponse.fromMessage(Messages.SEND_RESET_MAIL.formatMessage(request.email())));
    }

    @Override
    public ResponseEntity<MessageResponse> resetPassword(
            String token,
            ResetPasswordRequest request
    ) {
        Token resetToken = tokenRepository.findByToken(token)
                .orElseThrow(() -> new InvalidTokenException(Messages.INVALID_TOKEN.formatMessage()));

        User user = resetToken.getUser();

        if (!jwtService.validateToken(token, user))
            throw new TokenExpiredException(Messages.EXPIRED_TOKEN.formatMessage());

        user.setPassword(passwordEncoder.encode(request.password()));
        userRepository.save(user);

        resetToken.setExpired(true);
        resetToken.setRevoked(true);
        tokenRepository.save(resetToken);

        return ResponseEntity.ok()
                .body(MessageResponse.fromMessage(Messages.PASSWORD_RESET.formatMessage()));
    }
}