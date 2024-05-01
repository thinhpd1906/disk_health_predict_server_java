package com.example.disk_predict_server.service;
import com.example.disk_predict_server.api.dto.request.RegisterRequest;
import com.example.disk_predict_server.api.dto.request.VerifyCodeRequest;
import com.example.disk_predict_server.api.dto.response.AuthenticationResponse;
import com.example.disk_predict_server.persistence.token.Token;
import com.example.disk_predict_server.persistence.token.TokenRepository;
import com.example.disk_predict_server.persistence.token.TokenType;
import com.example.disk_predict_server.persistence.user.Role;
import com.example.disk_predict_server.persistence.user.User;
import com.example.disk_predict_server.persistence.user.UserRepository;
import com.example.disk_predict_server.utils.ConstantMessages;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import javax.ws.rs.InternalServerErrorException;


import java.io.IOException;
import java.util.*;

@Service
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {

    private final UserRepository userRepository;
    private final TokenRepository tokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    @Override
    public User register(RegisterRequest request) {
        User savedUser;
        Role roleUser =  Role.fromName(request.getRole());
//        SimpleDateFormat dateFormat =new SimpleDateFormat("dd/MM/yyyy");
//        Date userBrithDay = null;
//        if (request.getBirthDay() != null && !request.getBirthDay().isEmpty()) {
//            try {
//                userBrithDay = dateFormat.parse(request.getBirthDay());
//            } catch (ParseException e) {
//                e.printStackTrace();
//            }
//        }
        try {
            String verifyCode = generateVerifyCode(6);
            RegisterRequest a = request;
            long now = System.currentTimeMillis();
            User user = User.builder()
                    .fullName(request.getFullName())
                    .email(request.getEmail())
                    .password(passwordEncoder.encode(request.getPassword()))
//                    .birthDay(userBrithDay)
                    .role(roleUser)
                    .status("1")
                    .verifyCode(verifyCode)
                    .codeExpired(now + 5*60*1000)
                    .createAt(now)
                    .updateAt(now)
                    .build();
            savedUser = userRepository.save(user);
        } catch (DataIntegrityViolationException ex){
            DataIntegrityViolationException a = ex;
            return null;
        }
        return savedUser;
//        return AuthenticationResponse.builder()
//                .accessToken(jwtToken)
//                .refreshToken(refreshToken)
//                .build();
    }
//    public List<User> registerExcell(List<RegisterRequest> listRegisterRequest) {
//       for(RegisterRequest registerReq: listRegisterRequest) {
//           User savedUser;
//           Role roleUser =  Role.fromName(registerReq.getRole());
//           SimpleDateFormat dateFormat =new SimpleDateFormat("dd/MM/yyyy");
//           Date userBrithDay = null;
//           if (registerReq.getBirthDay() != null && !registerReq.getBirthDay().isEmpty()) {
//               try {
//                   userBrithDay = dateFormat.parse(registerReq.getBirthDay());
//               } catch (ParseException e) {
//                   e.printStackTrace();
//               }
//           }
//           try {
//               User user = User.builder()
//                       .fullName(registerReq.getFullName())
//                       .email(registerReq.getEmail())
//                       .password(passwordEncoder.encode(registerReq.getPassword()))
//                       .birthDay(userBrithDay)
//                       .role(roleUser)
//                       .build();
//               savedUser = userRepository.save(user);
//           } catch (DataIntegrityViolationException ex){
//               return null;
//           }
//       }
//
//    }

    @Override
    public User authenticate(String email, String password) {
        Authentication authentication;
        try {
            // authenticate user using authenticationManager

            authentication = authenticationManager
                    .authenticate(new UsernamePasswordAuthenticationToken(email , password));

        } catch (BadCredentialsException ex) { //authenticate failed
            return null;
        }
        var  user = userRepository.findByEmail(email)
                .orElseThrow();
        if (user == null) {
            throw new InternalServerErrorException(ConstantMessages.MESSAGE_SOMETHING_WENT_WRONG);
        }
        return user;
    }

    @Override
    public void saveUserToken(User user, String jwtToken) {
        var token = Token.builder()
                .user(user)
                .token(jwtToken)
                .tokenType(TokenType.BEARER)
                .expired(false)
                .revoked(false)
                .build();
        tokenRepository.save(token);
    }

    @Override
    public  void revokeAllUserTokens(User user) {
        var validUserTokens = tokenRepository.findAllValidTokenByUser(user.getId());
        if (validUserTokens.isEmpty())
            return;
        validUserTokens.forEach(token -> {
            token.setExpired(true);
            token.setRevoked(true);
        });
        tokenRepository.saveAll(validUserTokens);
    }
    @Transactional
    public void insertExcellUser(List<RegisterRequest> ListRequest) {
        List<User> listUser = new ArrayList<>();
        for(RegisterRequest request:ListRequest ) {
            User user = createUser(request);
//                entityManager.persist(user);
            listUser.add(user);
        }
        userRepository.saveAll(listUser);
    }
    private User createUser(RegisterRequest request) {
        User user = new User();
        user.setFullName(request.getFullName());
        user.setEmail(request.getEmail());
        user.setPassword(request.getPassword());
        user.setRole(Role.fromName(request.getRole()));
        return user;
    }
    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public void refreshToken(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws IOException {
        final String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        final String refreshToken;
        final String userEmail;
        if (authHeader == null ||!authHeader.startsWith("Bearer ")) {
            return;
        }
        refreshToken = authHeader.substring(7);
        userEmail = jwtService.extractUsername(refreshToken);
        if (userEmail != null) {
            var user = this.userRepository.findByEmail(userEmail)
                    .orElseThrow();
            if (jwtService.isTokenValid(refreshToken, user)) {
                var accessToken = jwtService.generateToken(user);
                revokeAllUserTokens(user);
                saveUserToken(user, accessToken);
                var authResponse = AuthenticationResponse.builder()
                        .accessToken(accessToken)
                        .refreshToken(refreshToken)
                        .build();
                new ObjectMapper().writeValue(response.getOutputStream(), authResponse);
            }
        }
    }
    @Override
    public String handleLoginSuccess(User user) {
        String jwtToken = jwtService.generateToken(user);
        if (jwtToken == null) {
            throw new InternalServerErrorException(ConstantMessages.MESSAGE_SOMETHING_WENT_WRONG);
        }
//        var refreshToken = jwtService.generateRefreshToken(user);
        saveUserToken(user, jwtToken);

        // store generated token into redis: key=email+current time, value=token with timeout
//        redisTemplate.ohandlepsForValue().set(user.getEmail() + jwtUtils.getTime(jwtToken).toString(), jwtToken, jwtUtils.getExpiration(), TimeUnit.MILLISECONDS);
//        // reset info about login failed
//        redisTemplate.delete(user.getEmail() + this.redisLoginFailedPostfix);

        return jwtToken;
    }
    @Override
    public boolean checkEmailExist(String email) {
        return userRepository.existsByEmail(email);
    }
    @Override
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }
    @Override
    public Integer getUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) authentication.getPrincipal();
        Integer userId = user.getId();
        String id = userId.toString();
        System.out.println("userId" +  id);
        return userId;
    }
    @Override
    public User getUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) authentication.getPrincipal();
        return user;
    }

    @Override
    public String generateVerifyCode(int length) {
// Dãy ký tự số từ '0' đến '9'
        String digits = "0123456789";

        // Sử dụng StringBuilder để xây dựng chuỗi kết quả
        StringBuilder stringBuilder = new StringBuilder(length);

        // Tạo đối tượng Random
        Random random = new Random();

        // Tạo chuỗi ngẫu nhiên bằng cách chọn ngẫu nhiên các ký tự từ dãy số
        for (int i = 0; i < length; i++) {
            int randomIndex = random.nextInt(digits.length());
            char randomDigit = digits.charAt(randomIndex);
            stringBuilder.append(randomDigit);
        }

        return stringBuilder.toString();
    }

    public String checkStatusAccount(String email) {
        Optional<User> user = findByEmail(email);
        if(user.isPresent()) {
            String status = user.get().getStatus();
            if(status.equals("0")) {
                return "userDisabled";
            } else if(status.equals("1")) {
                return "userNotYetVerify";
            } else if(status.equals("2")) {
                return "userValid";
            }
        }
        return "userNotFound";
    }

    @Override
    public void verifyCode(VerifyCodeRequest verifyCodeRequest) {
        if(!checkEmailExist(verifyCodeRequest.getEmail())) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "emailNotYetRegisted");
        Optional<User> user = findByEmail(verifyCodeRequest.getEmail());
        User userPresent;
        if(user.isPresent()) {
            userPresent = user.get();
            if(!userPresent.getVerifyCode().equals(verifyCodeRequest.getVerifyCode())) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "verifyCodeWrong");
            }
            userPresent.setStatus("2");
            userRepository.save(userPresent);
        }

    }
}
