package com.example.disk_predict_server.persistence.user;


import com.example.disk_predict_server.persistence.token.Token;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import java.util.Collection;
import java.util.List;

import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

//@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "user")
@Getter
@Setter
public class User implements UserDetails {

    @Id
//    @GeneratedValue
    private String id;
    private String fullName;
    private String email;
    @JsonIgnore
    private String password;
    //    private Date birthDay;
    @Enumerated(EnumType.STRING)
    private Role role;
//    @JsonIgnore
//    private Integer active;
    private Long createAt;
    private Long updateAt;
//    private String verifyCode;
    //    Status: 0: deleted, 1: Registered but not yet verify code, 2: ok
//    private String status;
//    private Long codeExpired;
    @OneToMany(mappedBy = "user")
    private List<Token> tokens;
//    public String getId() {
//        return id;
//    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return role.getAuthorities();
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
