package fr.bio.apiauthentication.dto.admin;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@DisplayName("Test role request DTO")
public class RoleRequestTest {
    private static final ObjectMapper mapper = new ObjectMapper();

    private RoleRequest request;

    private String authority;
    private String displayName;
    private String description;

    @BeforeEach
    void setUp() {
        authority = RandomStringUtils.randomAlphabetic(10).toUpperCase();
        displayName = RandomStringUtils.randomAlphabetic(20);
        description = RandomStringUtils.randomAlphabetic(20);

        request = new RoleRequest(authority, displayName, description);
    }

    @AfterEach
    void tearDown() {
        request = null;
    }

    @Test
    public void testRecordFields() {
        assertThat(request).isNotNull();
        assertThat(request.authority()).isEqualTo(authority);
        assertThat(request.displayName()).isEqualTo(displayName);
        assertThat(request.description()).isEqualTo(description);
    }

    @Test
    public void testEquals() {
        RoleRequest requestEquals = new RoleRequest(authority, displayName, description );

        assertThat(request).isEqualTo(requestEquals);
    }

    @Test
    public void testNotEquals() {
        RoleRequest requestNotEquals = new RoleRequest(
                RandomStringUtils.randomAlphanumeric(20).toUpperCase(),
                RandomStringUtils.randomAlphanumeric(20),
                RandomStringUtils.randomAlphanumeric(20)
        );

        assertThat(request).isNotEqualTo(requestNotEquals);
    }

    @Test
    public void testSerialize() throws Exception {
        String json = mapper.writeValueAsString(request);
        RoleRequest actualRequest = mapper.readValue(json, RoleRequest.class);

        String expectedJson = "{" +
                "\"authority\":\"" + authority + "\"," +
                "\"display_name\":\"" + displayName + "\"," +
                "\"description\":\"" + description + "\"" +
                "}";
        RoleRequest expectedRequest = mapper.readValue(expectedJson, RoleRequest.class);

        assertThat(actualRequest).isEqualTo(expectedRequest);
    }

    @Test
    public void testDeserialize() throws Exception {
        String json = "{" +
                "\"authority\":\"" + authority + "\"," +
                "\"display_name\":\"" + displayName + "\"," +
                "\"description\":\"" + description + "\"" +
                "}";
        RoleRequest requestMapped = mapper.readValue(json, RoleRequest.class);

        assertThat(requestMapped).usingRecursiveComparison().isEqualTo(request);
    }
}