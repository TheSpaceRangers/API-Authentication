package fr.bio.apiauthentication.components;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@DisplayName("Test http headers")
public class HttpHeadersUtilTest {
    private final HttpHeadersUtil httpHeadersUtil = new HttpHeadersUtil();

    @Test
    public void testCreateHeaders() {
        String token = "mock-token";

        HttpHeaders headers = httpHeadersUtil.createHeaders(token);

        assertThat(headers).isNotNull();
        assertThat(headers.getFirst(HttpHeaders.AUTHORIZATION)).isEqualTo("Bearer " + token);
        assertThat(headers.getFirst(HttpHeaders.CONTENT_TYPE)).isEqualTo("application/json");
        assertThat(headers.get("Access-Control-Expose-Headers")).isEqualTo(List.of("Authorization"));
    }
}
