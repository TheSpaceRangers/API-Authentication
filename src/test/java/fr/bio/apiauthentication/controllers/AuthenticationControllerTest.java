package fr.bio.apiauthentication.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;

import fr.bio.apiauthentication.config.SecurityConfiguration;
import fr.bio.apiauthentication.dto.AuthenticationResponse;
import fr.bio.apiauthentication.dto.CreateUserRequest;
import fr.bio.apiauthentication.services.IAuthenticationService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import org.mockito.Mockito;
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

    private CreateUserRequest request;
    private AuthenticationResponse response;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);

        request = new CreateUserRequest(
                "John",
                "Doe",
                "john.doe@example.com",
                "password"
        );

        response = new AuthenticationResponse(
                "L'utilisateur john.doe@example.com a bien été créé !"
        );
    }

    @Test
    public void testRegister() throws Exception {
        when(authenticationService.register(any(CreateUserRequest.class))).thenReturn(ResponseEntity.ok(response));

        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                ).andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(objectMapper.writeValueAsString(response)));
    }
}
