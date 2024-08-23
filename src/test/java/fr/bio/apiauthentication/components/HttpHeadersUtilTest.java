package fr.bio.apiauthentication.components;

import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpHeaders;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@DisplayName("Test http headers")
public class HttpHeadersUtilTest {
    private HttpHeadersUtil httpHeadersUtil;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        httpHeadersUtil = new HttpHeadersUtil();
    }

    @Test
    public void testCreateHeaders() {
        String randomToken = RandomStringUtils.randomAlphanumeric(20);

        HttpHeaders headers = httpHeadersUtil.createHeaders(randomToken);

        assertThat(headers).isNotNull();
        assertThat(headers.getFirst("Authorization")).isEqualTo("Bearer " + randomToken);
        assertThat(headers.getFirst("Content-Type")).isEqualTo("application/json");
        assertThat(headers.getFirst("Access-Control-Expose-Headers")).isEqualTo("Authorization");
    }
}
