package fr.bio.apiauthentication.services.interfaces;

import fr.bio.apiauthentication.dto.admin.LoginHistoryRequest;
import fr.bio.apiauthentication.dto.admin.LoginHistoryStructureResponse;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface IAdminLoginHistoryService {
    ResponseEntity<List<LoginHistoryStructureResponse>> getAllLoginHistory(String token);

    ResponseEntity<List<LoginHistoryStructureResponse>> getAllLoginHistoryByUser(String token, LoginHistoryRequest request);
}
