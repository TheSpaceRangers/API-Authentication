package fr.bio.apiauthentication.components;

import fr.bio.apiauthentication.entities.LoginHistory;
import fr.bio.apiauthentication.entities.User;
import fr.bio.apiauthentication.repositories.LoginHistoryRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.RandomStringUtils;
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
import static org.mockito.Mockito.when;

@DisplayName("Test authentication success listener")
public class AuthenticationSuccessListenerTest {
    @InjectMocks
    private AuthenticationSuccessListener listener;

    @Mock
    private LoginHistoryRepository loginHistoryRepository;

    @Mock
    private HttpServletRequest request;

    @Mock
    WebAuthenticationDetails webDetails;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    private String email;
    private String password;
    private String firstName;
    private String lastName;

    @BeforeEach
    void init() {
        email = RandomStringUtils.randomAlphanumeric(10) + "@test.com";
        password = RandomStringUtils.randomAlphanumeric(10);
        firstName = RandomStringUtils.randomAlphabetic(8);
        lastName = RandomStringUtils.randomAlphabetic(8);
    }

    @Test
    public void testOnApplicationEvent() {
        final User user = User.builder()
                .email(email)
                .password(password)
                .firstName(firstName)
                .lastName(lastName)
                .enabled(true)
                .roles(null)
                .build();

        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(user, null);
        authentication.setDetails(webDetails);

        AuthenticationSuccessEvent event = new AuthenticationSuccessEvent(authentication);

        when(request.getRemoteAddr()).thenReturn("127.0.0.1");
        when(webDetails.getRemoteAddress()).thenReturn("127.0.0.1");

        listener.onApplicationEvent(event);

        ArgumentCaptor<LoginHistory> captor = ArgumentCaptor.forClass(LoginHistory.class);
        verify(loginHistoryRepository).save(captor.capture());
        LoginHistory savedLoginHistory = captor.getValue();

        assertThat(savedLoginHistory).isNotNull();
        assertThat(savedLoginHistory.getUser()).isEqualTo(user);
        assertThat(savedLoginHistory.getDateLogin()).isNotNull();
        assertThat(savedLoginHistory.getIpAddress()).isEqualTo("127.0.0.1");
    }

    @Test
    public void testOnApplicationEvent_WithoutWebAuthenticationDetails() {
        final User user = User.builder()
                .email(email)
                .password(password)
                .firstName(firstName)
                .lastName(lastName)
                .enabled(true)
                .roles(null)
                .build();

        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(user, null);

        AuthenticationSuccessEvent event = new AuthenticationSuccessEvent(authentication);

        when(request.getRemoteAddr()).thenReturn("127.0.0.1");

        listener.onApplicationEvent(event);

        ArgumentCaptor<LoginHistory> captor = ArgumentCaptor.forClass(LoginHistory.class);
        verify(loginHistoryRepository).save(captor.capture());
        LoginHistory savedLoginHistory = captor.getValue();

        assertThat(savedLoginHistory).isNotNull();
        assertThat(savedLoginHistory.getUser()).isEqualTo(user);
        assertThat(savedLoginHistory.getDateLogin()).isNotNull();
        assertThat(savedLoginHistory.getIpAddress()).isEqualTo("127.0.0.1");
    }
}