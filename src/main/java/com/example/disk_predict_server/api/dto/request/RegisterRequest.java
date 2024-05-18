package com.example.disk_predict_server.api.dto.request;

import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class RegisterRequest {
    private String id;
    private String fullName;
    private String email;
    private String password;
    //    private String birthDay;
    private String role;
}