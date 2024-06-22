package com.example.disk_predict_server.service;
import com.example.disk_predict_server.api.dto.request.RegisterRequest;
import com.example.disk_predict_server.persistence.model.user.User;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

public interface AuthenticationService {


    User register(RegisterRequest request);

    User authenticate(String email, String password);
    void insertExcellUser(List<RegisterRequest> ListRequest);

    void saveUserToken(User user, String jwtToken);

    void revokeAllUserTokens(User user);

    void refreshToken(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws IOException;
    String handleLoginSuccess(User user);
    boolean checkEmailExist(String email);
    boolean checkIdExist(String id);
    Optional<User> findByEmail(String email);String
     getUserId();
    User getUser();
//    String generateVerifyCode(int length);
//    String checkStatusAccount(String email);
//    void verifyCode(VerifyCodeRequest verifyCodeRequest);
}
