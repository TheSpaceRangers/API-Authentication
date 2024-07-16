package fr.bio.apiauthentication;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class ApiAuthenticationApplication {

    public static void main(String[] args) {
        SpringApplication.run(ApiAuthenticationApplication.class, args);
    }
}