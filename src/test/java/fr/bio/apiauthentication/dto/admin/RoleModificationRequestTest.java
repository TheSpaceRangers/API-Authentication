package fr.bio.apiauthentication.dto.admin;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@DisplayName("Test update role DTO")
public class RoleModificationRequestTest {
    private static final ObjectMapper mapper = new ObjectMapper();

    private RoleModificationRequest request;

    @BeforeEach
    void setUp() {
        request = new RoleModificationRequest("USER", "USER", "" );
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
        RoleModificationRequest requestEquals = new RoleModificationRequest("USER", "USER", "" );

        assertThat(request).isEqualTo(requestEquals);
    }

    @Test
    public void testNotEquals() {
        RoleModificationRequest requestNotEquals = new RoleModificationRequest("ADMIN", "USER", "" );

        assertThat(request).isNotEqualTo(requestNotEquals);
    }

    @Test
    public void testSerialize() throws Exception {
        String json = mapper.writeValueAsString(request);
        RoleModificationRequest actualRequest = mapper.readValue(json, RoleModificationRequest.class);

        String expectedJson = "{" +
                "\"authority\":\"USER\"," +
                "\"display_name\":\"USER\"," +
                "\"description\":\"\"" +
                "}";
        RoleModificationRequest expectedRequest = mapper.readValue(expectedJson, RoleModificationRequest.class);

        assertThat(actualRequest).isEqualTo(expectedRequest);
    }

    @Test
    public void testDeserialize() throws Exception {
        String json = "{" +
                "\"authority\":\"USER\"," +
                "\"display_name\":\"USER\"," +
                "\"description\":\"\"" +
                "}";
        RoleModificationRequest requestMapped = mapper.readValue(json, RoleModificationRequest.class);

        assertThat(requestMapped).usingRecursiveComparison().isEqualTo(request);
    }
}
