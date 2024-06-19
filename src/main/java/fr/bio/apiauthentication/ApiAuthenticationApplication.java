package fr.bio.apiauthentication;

import fr.bio.apiauthentication.entities.Role;
import fr.bio.apiauthentication.repositories.RoleRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class ApiAuthenticationApplication {

    public static void main(String[] args) {
        SpringApplication.run(ApiAuthenticationApplication.class, args);
    }

    @Bean
    CommandLineRunner commandLineRunner(RoleRepository roleRepository) {
        return args -> {
            if (roleRepository.findAll().isEmpty()) {
                Role role = Role.builder()
                        .roleName("USER")
                        .build();
                roleRepository.save(role);
            }
        };
    }
}
