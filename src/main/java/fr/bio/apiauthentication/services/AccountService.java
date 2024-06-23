package fr.bio.apiauthentication.services;

import fr.bio.apiauthentication.components.HttpHeadersUtil;
import fr.bio.apiauthentication.dto.MessageResponse;
import fr.bio.apiauthentication.dto.account.UpdatePasswordRequest;
import fr.bio.apiauthentication.dto.account.UpdateUserProfilRequest;
import fr.bio.apiauthentication.dto.account.UserProfilResponse;
import fr.bio.apiauthentication.entities.Role;
import fr.bio.apiauthentication.entities.User;
import fr.bio.apiauthentication.exceptions.InvalidPasswordException;
import fr.bio.apiauthentication.repositories.UserRepository;
import fr.bio.apiauthentication.services.interfaces.IAccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class AccountService implements IAccountService {
    private final UserRepository userRepository;

    private final JwtService jwtService;

    private final PasswordEncoder passwordEncoder;

    private final HttpHeadersUtil httpHeadersUtil;

    @Override
    public ResponseEntity<UserProfilResponse> getUserProfile(
            String token
    ) {
        final String email = jwtService.getUsernameFromToken(token);

        final User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User Not Found"));

        return ResponseEntity.ok()
                .headers(httpHeadersUtil.createHeaders(token))
                .body(UserProfilResponse.builder()
                        .firstName(user.getFirstName())
                        .lastName(user.getLastName())
                        .email(user.getEmail())
                        .roles(user.getRoles()
                                .stream()
                                .map(Role::getAuthority)
                                .collect(Collectors.toList())
                        )
                        .build()
                );
    }

    @Override
    public ResponseEntity<MessageResponse> updateProfile(
            String token,
            UpdateUserProfilRequest request
    ) {
        final String email = jwtService.getUsernameFromToken(token);

        final User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User Not Found"));

        if (request.firstName() != null && !request.firstName().isBlank() && !user.getFirstName().equals(request.firstName()))
            user.setFirstName(request.firstName());

        if (request.lastName() != null && !request.lastName().isEmpty() && !user.getLastName().equals(request.lastName()))
            user.setLastName(request.lastName());

        if (request.email() != null && !request.email().isEmpty() && !user.getEmail().equals(request.email()))
            user.setEmail(request.email());

        userRepository.save(user);

        return ResponseEntity.ok()
                .headers(httpHeadersUtil.createHeaders(token))
                .body(MessageResponse.builder()
                        .message("User account has been updated")
                        .build()
                );
    }

    @Override
    public ResponseEntity<MessageResponse> updatePassword(
            String token,
            UpdatePasswordRequest request
    ) {
        final String email = jwtService.getUsernameFromToken(token);

        final User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User Not Found"));

        if (!passwordEncoder.matches(request.oldPassword(), user.getPassword()))
            throw new InvalidPasswordException("Invalid old password provided");

        user.setPassword(passwordEncoder.encode(request.newPassword()));
        userRepository.save(user);

        return ResponseEntity.ok()
                .headers(httpHeadersUtil.createHeaders(token))
                .body(MessageResponse.builder()
                        .message("User password has been changed")
                        .build()
                );
    }

    @Override
    public ResponseEntity<MessageResponse> deactivateAccount(
            String token
    ) {
        final String email = jwtService.getUsernameFromToken(token);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User Not Found"));

        user.setEnabled(false);
        userRepository.save(user);

        return ResponseEntity.ok()
                .headers(httpHeadersUtil.createHeaders(token))
                .body(MessageResponse.builder()
                        .message("User account has been deactivated")
                        .build()
                );
    }
}
