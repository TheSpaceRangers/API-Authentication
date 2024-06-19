package fr.bio.apiauthentication.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;

import fr.bio.apiauthentication.config.SecurityConfiguration;
import fr.bio.apiauthentication.dto.authentication.AuthenticationRequest;
import fr.bio.apiauthentication.dto.authentication.AuthenticationResponse;
import fr.bio.apiauthentication.dto.authentication.CreateUserRequest;
import fr.bio.apiauthentication.entities.Role;
import fr.bio.apiauthentication.repositories.RoleRepository;
import fr.bio.apiauthentication.services.interfaces.IAuthenticationService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import org.mockito.MockitoAnnotations;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("Test Authentication Controller")
@WebMvcTest(AuthenticationController.class)
@Import(SecurityConfiguration.class)
public class AuthenticationControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private IAuthenticationService authenticationService;

    @MockBean
    private RoleRepository roleRepository;

    private CreateUserRequest requestRegister;
    private AuthenticationRequest resquestLogin;
    private AuthenticationResponse responseRegister, responseLogin;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        Role role = Role.builder()
                .roleName("USER")
                .build();
        roleRepository.save(role);

        requestRegister = new CreateUserRequest(
                "John",
                "Doe",
                "john.doe@example.com",
                "password"
        );

        responseRegister = new AuthenticationResponse(
                "L'utilisateur john.doe@example.com a bien été créé !"
        );

        resquestLogin = new AuthenticationRequest(
                "john.doe@example.com",
                "password"
        );

        responseLogin = new AuthenticationResponse(
                "L'utilisateur john.doe@example.com est connecté !"
        );
    }

    @Test
    public void testRegister() throws Exception {
        when(authenticationService.register(any(CreateUserRequest.class))).thenReturn(ResponseEntity.ok(responseRegister));

        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestRegister))
                ).andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(objectMapper.writeValueAsString(responseRegister)));
    }

    @Test
    public void testLogin() throws Exception {
        when(authenticationService.login(any(AuthenticationRequest.class))).thenReturn(ResponseEntity.ok(responseLogin));

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(resquestLogin))
                ).andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(objectMapper.writeValueAsString(responseLogin)));
    }
}
