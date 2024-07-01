package fr.bio.apiauthentication.dto.admin;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@DisplayName("Test role request DTO")
public class RoleRequestTest {
    private static final ObjectMapper mapper = new ObjectMapper();

    private RoleRequest request;

    @BeforeEach
    void setUp() {
        request = new RoleRequest("USER", "USER", "" );
    }

    @AfterEach
    void tearDown() {
        request = null;
    }

    @Test
    public void testRecordFields() {
        assertThat(request.authority()).isEqualTo("USER");
    }

    @Test
    public void testEquals() {
        RoleRequest requestEquals = new RoleRequest("USER", "USER", "" );

        assertThat(request).isEqualTo(requestEquals);
    }

    @Test
    public void testNotEquals() {
        RoleRequest requestNotEquals = new RoleRequest("ADMIN", "USER", "" );

        assertThat(request).isNotEqualTo(requestNotEquals);
    }

    @Test
    public void testSerialize() throws Exception {
        String json = mapper.writeValueAsString(request);
        RoleRequest actualRequest = mapper.readValue(json, RoleRequest.class);

        String expectedJson = "{" +
                "\"authority\":\"USER\"," +
                "\"display_name\":\"USER\"," +
                "\"description\":\"\"" +
                "}";
        RoleRequest expectedRequest = mapper.readValue(expectedJson, RoleRequest.class);

        assertThat(actualRequest).isEqualTo(expectedRequest);
    }

    @Test
    public void testDeserialize() throws Exception {
        String json = "{" +
                "\"authority\":\"USER\"," +
                "\"display_name\":\"USER\"," +
                "\"description\":\"\"" +
                "}";
        RoleRequest requestMapped = mapper.readValue(json, RoleRequest.class);

        assertThat(requestMapped).usingRecursiveComparison().isEqualTo(request);
    }
}
