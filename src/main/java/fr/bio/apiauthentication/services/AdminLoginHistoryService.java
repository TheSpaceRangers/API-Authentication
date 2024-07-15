package fr.bio.apiauthentication.services;

import fr.bio.apiauthentication.components.HttpHeadersUtil;
import fr.bio.apiauthentication.dto.admin.LoginHistoryRequest;
import fr.bio.apiauthentication.dto.admin.LoginHistoryStructureResponse;
import fr.bio.apiauthentication.entities.LoginHistory;
import fr.bio.apiauthentication.entities.User;
import fr.bio.apiauthentication.enums.Messages;
import fr.bio.apiauthentication.repositories.LoginHistoryRepository;
import fr.bio.apiauthentication.repositories.UserRepository;
import fr.bio.apiauthentication.services.interfaces.IAdminLoginHistoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class AdminLoginHistoryService implements IAdminLoginHistoryService {
    private static final String USER = "User";

    private final LoginHistoryRepository loginHistoryRepository;
    private final UserRepository userRepository;

    private final HttpHeadersUtil httpHeadersUtil;

    @Override
    public ResponseEntity<List<LoginHistoryStructureResponse>> getAllLoginHistory(
            String token
    ) {
        final List<LoginHistory> loginHistories = loginHistoryRepository.findAll();

        return ResponseEntity.ok()
                .headers(httpHeadersUtil.createHeaders(token))
                .body(LoginHistoryStructureResponse.fromLoginHistories(loginHistories));
    }

    @Override
    public ResponseEntity<List<LoginHistoryStructureResponse>> getAllLoginHistoryByUser(
            String token, LoginHistoryRequest request
    ) {
        final User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new UsernameNotFoundException(Messages.ENTITY_NOT_FOUND.formatMessage(USER, request.email())));

        final List<LoginHistory> loginHistories = loginHistoryRepository.findAllByUser(user);

        return ResponseEntity.ok()
                .headers(httpHeadersUtil.createHeaders(token))
                .body(LoginHistoryStructureResponse.fromLoginHistories(loginHistories));
    }
}