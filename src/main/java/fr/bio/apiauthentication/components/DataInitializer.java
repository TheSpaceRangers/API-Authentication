package fr.bio.apiauthentication.components;

import fr.bio.apiauthentication.entities.*;
import fr.bio.apiauthentication.repositories.*;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class DataInitializer {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    private final PasswordEncoder passwordEncoder;

    @Bean
    CommandLineRunner initDatabase() {
        return args -> {
            final Role admin_role = roleRepository.findByAuthority("ADMIN").orElseGet(() -> {
                Role role = Role.builder()
                        .authority("ADMIN")
                        .displayName("Administrateur")
                        .description("")
                        .build();
                return roleRepository.save(role);
            });

            final Role user_role = roleRepository.findByAuthority("USER").orElseGet(() -> {
                Role role = Role.builder()
                        .authority("USER")
                        .displayName("Utilisateur")
                        .description("")
                        .build();
                return roleRepository.save(role);
            });

            final Role collaborator_role = roleRepository.findByAuthority("COLLABORATOR").orElseGet(() -> {
                Role role = Role.builder()
                        .authority("COLLABORATOR")
                        .displayName("Collaborateur")
                        .description("")
                        .build();
                return roleRepository.save(role);
            });

            final Role manager_role = roleRepository.findByAuthority("MANAGER").orElseGet(() -> {
                Role role = Role.builder()
                        .authority("MANAGER")
                        .displayName("Responsable")
                        .description("")
                        .build();
                return roleRepository.save(role);
            });

            final User ADMIN_USER = userRepository.findByEmail("c.tronel@beneteauhabitat.com").orElseGet(() -> {
                User admin = User.builder()
                        .firstName("TRONEL")
                        .lastName("Charles")
                        .email("c.tronel@beneteauhabitat.com")
                        .password(passwordEncoder.encode("JeSuisNeeLe15/11/99@Armentieres"))
                        .roles(List.of(admin_role))
                        .build();
                return userRepository.save(admin);
            });
        };
    }
}