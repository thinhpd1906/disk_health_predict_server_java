package com.example.disk_predict_server.api;

import com.example.disk_predict_server.api.dto.request.AuthenticationRequest;
import com.example.disk_predict_server.api.dto.request.RegisterRequest;
import com.example.disk_predict_server.persistence.user.User;
import com.example.disk_predict_server.service.AuthenticationService;
import com.example.disk_predict_server.service.EmailService;
import com.example.disk_predict_server.utils.ConstantMessages;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.HashMap;
import java.util.Map;
import org.json.simple.JSONObject;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@Tag(name="Authentication")
@RequiredArgsConstructor
public class AuthenticationController {
    @Autowired
    private  EmailService emailService;
    @Autowired
    private  AuthenticationService service;
    @Operation(
            description = "authentication ",
            summary = "This is api authentication ",
            responses = {
                    @ApiResponse(
                            description = "Success",
                            responseCode = "200"
                    ),
            }

    )
    @PostMapping("/admin/register")
    public ResponseEntity<Object> register(
            @RequestBody RegisterRequest request
    ) {
        Map<String, Object> response = new HashMap<>();
        Map<String, Object> errors = new HashMap<>();
        String id = request.getId();
        String email = request.getEmail();
        String password = request.getPassword();
        String fullName = request.getFullName();
//        String birthDay = request.getBirthDay();
        String role = request.getRole();
        if (id == null || StringUtils.isBlank(id)) {
            errors.put(ConstantMessages.ID, ConstantMessages.ERROR_USER_ID_MUST_NOT_NULL);
        } else if(service.checkIdExist(id)) {
            errors.put(ConstantMessages.ID, ConstantMessages.ID_USER_EMAIL_EXISTED);
        } else {
            id = id.trim();
        }
        if(email== null|| StringUtils.isBlank(email)) {
            errors.put(ConstantMessages.EMAIL, ConstantMessages.ERROR_USER_EMAIL_MUST_NOT_NULL);
        }
        else if(service.checkEmailExist(email)) {
            errors.put(ConstantMessages.EMAIL, ConstantMessages.ERROR_USER_EMAIL_EXISTED);
        } else {
            email = email.trim();
        }
        if (password == null || StringUtils.isBlank(password)) {
            errors.put(ConstantMessages.PASSWORD, ConstantMessages.ERROR_USER_PASSWORD_MUST_NOT_NULL);
        } else {
            password = password.trim();
        }
        if (fullName == null || StringUtils.isBlank(fullName)) {
            errors.put(ConstantMessages.NAME, ConstantMessages.ERROR_USER_FULLNAME_MUST_NOT_NULL);
        } else {
            fullName = fullName.trim();
        }
        if (role == null || StringUtils.isBlank(role)) {
            role = "USER";
        }  else  {
            role = role.trim();
            if (!role.equals("USER") && !role.equals("ADMIN")) {
                errors.put(ConstantMessages.ROLE, ConstantMessages.ERROR_USER_ROLE_NOT_EXIST);
            }
        }
//        if(birthDay == null || StringUtils.isBlank(birthDay)) {
//            birthDay = birthDay.trim();
//        } else {
//            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yy");
//            sdf.setLenient(false); // Tắt tính linh hoạt của định dạng
//            try {
//                // Parse chuỗi thành đối tượng Date
//                Date date = sdf.parse(birthDay);
//
//                // Nếu không có lỗi, chuỗi đúng định dạng
//                System.out.println("Chuỗi đúng định dạng.");
//            } catch (ParseException e) {
//                // Nếu có lỗi, chuỗi không đúng định dạng
//                System.out.println("Chuỗi không đúng định dạng.");
//            }
//        }
        if (!errors.isEmpty()) {
            response.put(ConstantMessages.MESSAGE, ConstantMessages.MESSAGE_INVALID_INPUT);
            response.put(ConstantMessages.ERRORS, errors);
            return new ResponseEntity<>(new JSONObject(response), HttpStatus.BAD_REQUEST);
        }
        User user;
        try {
//            Role roleNameConvertRole = Role.fromName(role);
//            RegisterRequest newRegRquest = new RegisterRequest(fullName, email, password, birthDay, role);
            RegisterRequest newRegRquest = new RegisterRequest(id, fullName, email, password,  role);
            user = service.register(newRegRquest);
        } catch (Exception ex) {
            response.put(ConstantMessages.MESSAGE, ConstantMessages.MESSAGE_SOMETHING_WENT_WRONG);
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        if( user== null) {
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
//        String code = user.getVerifyCode();
//        String emailUser = user.getEmail();
//        emailService.sendEmail(emailUser, "verify code", code);
        response.put(ConstantMessages.MESSAGE, ConstantMessages.MESSAGE_USER_REGISTER_SUCCESS);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
    @PostMapping("/authenticate")
    public ResponseEntity<Object> authenticate(
            @RequestBody AuthenticationRequest request
    ) {
        Map<String, Object> response = new HashMap<>();
        Map<String, Object> errors = new HashMap<>();
        // invalid input: email or password null/empty
        String email = request.getEmail();
        if(email== null|| StringUtils.isBlank(email)) {
            errors.put(ConstantMessages.EMAIL, ConstantMessages.ERROR_USER_EMAIL_MUST_NOT_NULL);
        } else {
            email = email.trim();
        }
        String password = request.getPassword();
        if (password == null || StringUtils.isBlank(password)) {
            errors.put(ConstantMessages.PASSWORD, ConstantMessages.ERROR_USER_PASSWORD_MUST_NOT_NULL);
        } else {
            password = password.trim();
        }
        if (!errors.isEmpty()) {
            response.put(ConstantMessages.MESSAGE, ConstantMessages.MESSAGE_INVALID_INPUT);
            response.put(ConstantMessages.ERRORS, errors);
            return new ResponseEntity<>(new JSONObject(response), HttpStatus.BAD_REQUEST);
        }
//        String status = service.checkStatusAccount(email);
//        if(status != "userValid") {
//            response.put(ConstantMessages.MESSAGE, ConstantMessages.MESSAGE_INVALID_INPUT);
//            errors.put("cannotLogin", status);
//            response.put(ConstantMessages.ERRORS, errors);
//            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
//        }
        User user;
        try {
            user = service.authenticate(email, password);
        } catch (Exception ex) {
            response.put(ConstantMessages.MESSAGE, ConstantMessages.MESSAGE_SOMETHING_WENT_WRONG);
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        if( user== null) {
            return this.handleLoginFailed(email, response);
        }
        return this.handleLoginSuccess(user, response);
    }
    private Map<String, String> buildReturnedUser(User user) {
        Map<String, String> returnedUser = new HashMap<>();
        returnedUser.put("id", user.getId());
        returnedUser.put("email", user.getEmail());
        returnedUser.put("name", user.getFullName());
        returnedUser.put("role", user.getRole().getName());
        return returnedUser;
    }

    private ResponseEntity<Object> handleLoginFailed(String email, Map<String, Object> response) {
        response.put(ConstantMessages.MESSAGE, ConstantMessages.MESSAGE_INVALID_CREDENTIALS);
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    private ResponseEntity<Object> handleLoginSuccess(User user, Map<String, Object> response) {
        String jwtToken;
        try {
            jwtToken = service.handleLoginSuccess(user);
        } catch (Exception ex) {
            response.put(ConstantMessages.MESSAGE, ConstantMessages.MESSAGE_SOMETHING_WENT_WRONG);
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        response.put("access_token", jwtToken);
        response.put("user", this.buildReturnedUser(user));
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
