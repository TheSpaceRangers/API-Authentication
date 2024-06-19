package fr.bio.apiauthentication.services;

import fr.bio.apiauthentication.dto.account.UserProfilRequest;
import fr.bio.apiauthentication.dto.account.UserProfilResponse;
import fr.bio.apiauthentication.entities.Role;
import fr.bio.apiauthentication.entities.User;
import fr.bio.apiauthentication.repositories.UserRepository;
import fr.bio.apiauthentication.services.interfaces.IAccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor()
public class AccountService implements IAccountService {
    private final UserRepository userRepository;

    private final JwtService jwtService;

    @Override
    public ResponseEntity<UserProfilResponse> getUserProfile(
            UserProfilRequest request
    ) {
        final String email = jwtService.getUsernameFromToken(request.token());

        final User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User Not Found"));

        return ResponseEntity.ok()
                .body(UserProfilResponse.builder()
                        .firstName(user.getFirstName())
                        .lastName(user.getLastName())
                        .email(user.getEmail())
                        .roles(user.getRoles()
                                .stream()
                                .map(Role::getRoleName)
                                .collect(Collectors.toList())
                        )
                        .build()
                );
    }
}
