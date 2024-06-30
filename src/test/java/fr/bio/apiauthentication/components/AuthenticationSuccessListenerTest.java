package fr.bio.apiauthentication.components;

import fr.bio.apiauthentication.entities.LoginHistory;
import fr.bio.apiauthentication.entities.User;
import fr.bio.apiauthentication.repositories.LoginHistoryRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.security.web.authentication.WebAuthenticationDetails;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.verify;

@DisplayName("Test authentication success listener")
public class AuthenticationSuccessListenerTest {
    @InjectMocks
    private AuthenticationSuccessListener listener;

    @Mock
    private LoginHistoryRepository loginHistoryRepository;

    @Mock
    private HttpServletRequest request;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testOnApplicationEvent() {
        User user = User.builder()
                .email("c.tronel@test.properties.com")
                .password("password")
                .firstName("firstName")
                .lastName("lastName")
                .enabled(true)
                .roles(null)
                .build();

        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(user, null);
        authentication.setDetails(new WebAuthenticationDetails(request));

        AuthenticationSuccessEvent event = new AuthenticationSuccessEvent(authentication);

        listener.onApplicationEvent(event);

        ArgumentCaptor<LoginHistory> captor = ArgumentCaptor.forClass(LoginHistory.class);
        verify(loginHistoryRepository).save(captor.capture());
        LoginHistory savedLoginHistory = captor.getValue();

        assertThat(savedLoginHistory).isNotNull();
        assertThat(user).isEqualTo(savedLoginHistory.getUser());
        assertThat(savedLoginHistory.getDateLogin()).isNotNull();
    }
}