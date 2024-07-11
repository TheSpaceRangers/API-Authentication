package fr.bio.apiauthentication.dto.admin;

import com.fasterxml.jackson.annotation.JsonProperty;
import fr.bio.apiauthentication.entities.LoginHistory;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.ZoneOffset;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginHistoryStructureResponse {
    @JsonProperty("id_login_history")
    private long idLoginHistory;

    @JsonProperty("id_user")
    private long idUser;

    @JsonProperty("user_email")
    private String userEmail;

    @JsonProperty("date_login")
    private long dateLogin;

    public static LoginHistoryStructureResponse fromLoginHistory(
            LoginHistory loginHistory
    ) {
        return LoginHistoryStructureResponse.builder()
                .idLoginHistory(loginHistory.getIdLoginHistory())
                .idUser(loginHistory.getUser().getIdUser())
                .userEmail(loginHistory.getUser().getEmail())
                .dateLogin(loginHistory.getDateLogin().toInstant(ZoneOffset.UTC).toEpochMilli())
                .build();
    }

    public static List<LoginHistoryStructureResponse> fromLoginHistories(
            List<LoginHistory> loginHistories
    ) {
        return loginHistories != null
                ? loginHistories.stream()
                    .map(LoginHistoryStructureResponse::fromLoginHistory)
                    .toList()
                : List.of();
    }
}