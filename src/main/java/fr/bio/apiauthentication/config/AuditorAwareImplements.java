package fr.bio.apiauthentication.config;

import fr.bio.apiauthentication.entities.User;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Optional;

public class AuditorAwareImplements implements AuditorAware<String> {
    @Override
    public Optional<String> getCurrentAuditor() {
        return Optional.ofNullable(SecurityContextHolder.getContext().getAuthentication())
                .map(authentication -> {
                    if (authentication.getPrincipal() instanceof UserDetails) {
                        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
                        User user = (User) userDetails;
                        return user.getFirstName().charAt(0) + "." + user.getLastName();
                    }
                    return null;
                })
                .or(() -> Optional.of("system"));
    }
}
