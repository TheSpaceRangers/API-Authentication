package fr.bio.apiauthentication.dto.admin;

import com.fasterxml.jackson.databind.ObjectMapper;
import fr.bio.apiauthentication.dto.account.UpdateUserProfilRequest;
import fr.bio.apiauthentication.dto.account.UserProfilResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Collections;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@DisplayName("Test update role DTO")
public class UpdateRoleRequestTest {
    private static final ObjectMapper mapper = new ObjectMapper();

    private UpdateRoleRequest request;

    @BeforeEach
    void setUp() {
        request = new UpdateRoleRequest("USER", "USER", "" );
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
        UpdateRoleRequest requestEquals = new UpdateRoleRequest("USER", "USER", "" );

        assertThat(request).isEqualTo(requestEquals);
    }

    @Test
    public void testNotEquals() {
        UpdateRoleRequest requestNotEquals = new UpdateRoleRequest("ADMIN", "USER", "" );

        assertThat(request).isNotEqualTo(requestNotEquals);
    }

    @Test
    public void testSerialize() throws Exception {
        String json = mapper.writeValueAsString(request);
        UpdateRoleRequest actualRequest = mapper.readValue(json, UpdateRoleRequest.class);

        String expectedJson = "{" +
                "\"authority\":\"USER\"," +
                "\"display_name\":\"USER\"," +
                "\"description\":\"\"" +
                "}";
        UpdateRoleRequest expectedRequest = mapper.readValue(expectedJson, UpdateRoleRequest.class);

        assertThat(actualRequest).isEqualTo(expectedRequest);
    }

    @Test
    public void testDeserialize() throws Exception {
        String json = "{" +
                "\"authority\":\"USER\"," +
                "\"display_name\":\"USER\"," +
                "\"description\":\"\"" +
                "}";
        UpdateRoleRequest requestMapped = mapper.readValue(json, UpdateRoleRequest.class);

        assertThat(requestMapped).usingRecursiveComparison().isEqualTo(request);
    }
}
