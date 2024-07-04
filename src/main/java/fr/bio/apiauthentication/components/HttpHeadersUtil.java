package fr.bio.apiauthentication.components;

import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;

@Component
public class HttpHeadersUtil {
    private static final String EXPOSE_HEADERS = "Access-Control-Expose-Headers";
    private static final String EXPOSE_HEADERS_LIST = "Authorization";

    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String BEARER_PREFIX = "Bearer ";

    private static final String CONTENT_TYPE_HEADER = "Content-Type";
    private static final String CONTENT_TYPE_JSON = "application/json";

    public HttpHeaders createHeaders(
            String token
    ) {
        HttpHeaders headers = new HttpHeaders();
        headers.add(EXPOSE_HEADERS, EXPOSE_HEADERS_LIST);
        headers.set(AUTHORIZATION_HEADER, BEARER_PREFIX + token);
        headers.set(CONTENT_TYPE_HEADER, CONTENT_TYPE_JSON);
        return headers;
    }
}