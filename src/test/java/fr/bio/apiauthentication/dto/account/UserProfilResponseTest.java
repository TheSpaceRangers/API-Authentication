package fr.bio.apiauthentication.dto.account;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Collections;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@DisplayName("Test user profile DTO Response")
public class UserProfilResponseTest {
    private static final ObjectMapper mapper = new ObjectMapper();

    private UserProfilResponse response;

    @BeforeEach
    void setUp() {
        response = UserProfilResponse.builder()
                .firstName("firstName")
                .lastName("lastName")
                .email("email")
                .roles(Collections.singletonList("USER"))
                .build();
    }

    @AfterEach
    void tearDown() {
        response = null;
    }

    @Test
    public void testEquals() {
        UserProfilResponse reponseEquals = UserProfilResponse.builder()
                .firstName("firstName")
                .lastName("lastName")
                .email("email")
                .roles(Collections.singletonList("USER"))
                .build();

        assertThat(response).isEqualTo(reponseEquals);
    }

    @Test
    public void testNotEquals() {
        UserProfilResponse responseNotEquals = UserProfilResponse.builder()
                .firstName("")
                .lastName("lastName")
                .email("email")
                .roles(Collections.singletonList("USER"))
                .build();

        assertThat(response).isNotEqualTo(responseNotEquals);
    }

    @Test
    public void testSerialize() throws Exception {
        String json = mapper.writeValueAsString(response);
        String expectedJson = "{" +
                "\"first_name\":\"firstName\"," +
                "\"last_name\":\"lastName\"," +
                "\"email\":\"email\"," +
                "\"roles\":[\"USER\"]" +
                "}";

        assertThat(json).isEqualTo(expectedJson);
    }

    @Test
    public void testDeserialize() throws Exception {
        String json = "{" +
                "\"first_name\":\"firstName\"," +
                "\"last_name\":\"lastName\"," +
                "\"email\":\"email\"," +
                "\"roles\":[\"USER\"]" +
                "}";
        UserProfilResponse responseMapped = mapper.readValue(json, UserProfilResponse.class);

        assertThat(responseMapped).usingRecursiveComparison().isEqualTo(response);
    }
}
