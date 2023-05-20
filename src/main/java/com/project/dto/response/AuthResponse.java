package com.project.dto.response;

import com.project.model.User;
import com.project.response.ResponseResult;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AuthResponse extends ResponseResult {
    private String token;
    private Long id;
    private String username;
    private String name;
    private String address;
    private String roleName;
    private String email;
    private String phone;
    private Long shopId;
    private Long walletId;
}
