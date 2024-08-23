package fr.bio.apiauthentication.config;

import fr.bio.apiauthentication.entities.User;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Optional;

public class AuditorAwareImplements implements AuditorAware<String> {
    private static final String DEFAULT_USERNAME = "system";

    @Override
    public Optional<String> getCurrentAuditor() {
        return Optional.ofNullable(SecurityContextHolder.getContext().getAuthentication())
                .map(authentication -> {
                    Object principal = authentication.getPrincipal();
                    if (principal instanceof UserDetails userDetails) {
                        return extractAuditorName(userDetails);
                    }
                    return null;
                })
                .or(() -> Optional.of(DEFAULT_USERNAME));
    }

    private String extractAuditorName(UserDetails userDetails) {
        if (userDetails instanceof User user)
            return user.getFirstName().charAt(0) + "." + user.getLastName();

        return userDetails.getUsername();
    }
}