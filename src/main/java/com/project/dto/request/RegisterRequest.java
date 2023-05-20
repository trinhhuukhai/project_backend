package com.project.dto.request;

import com.project.model.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RegisterRequest {

    private String username;
    private String password;
    private String name;
    private String email;
    private String address;
    private String phone;
    private Role role;
    private Long walletId;
}
