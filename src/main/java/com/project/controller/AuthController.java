package com.project.controller;

import com.project.dto.request.AuthRequest;
import com.project.dto.request.RegisterRequest;
import com.project.model.*;
import com.project.repository.TokenRepository;
import com.project.repository.UserRepository;
import com.project.response.ResponseCode;
import com.project.response.ResponseResult;
import com.project.service.*;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
@CrossOrigin("http://localhost:3000")

public class AuthController {

    public final AuthService service;

    @Autowired
    private ProductService productService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private TokenRepository tokenRepository;

    @Autowired
    private OrderService orderService;

    @Autowired
    private UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    @Autowired
    private EmailService emailService;
    @Autowired
    private TokenService tokenService;

//    @PostMapping("/register")
    @RequestMapping(value = "/register")
    public ResponseEntity<ResponseResult> register(@RequestBody RegisterRequest request){
        return service.register(request);
    }

    @PostMapping("/authenticate")
    public ResponseEntity<ResponseResult> authenticate(@RequestBody AuthRequest request) {
        return service.login(request);
    }
//    @CrossOrigin("http://localhost:3000")
    @GetMapping({"/getAllUser"})
    @PreAuthorize("hasAuthority('ADMIN')")
    List<User> getAll(){
        return (List<User>) service.getAllUser();
    }


    @GetMapping("/logout")
    public ResponseResult logout(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (org.springframework.util.StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            tokenService.deleteToken(bearerToken.substring(7));
            return ResponseResult.success("Thành công");
        }
        return new ResponseResult(ResponseCode.ERROR);
    }

    @GetMapping("/{id}/order")
    ResponseEntity<ResponseResult> findOByUserId(@PathVariable Long id) {
        return orderService.findOrderByUserId(id);
    }



    @PostMapping("/forgot-password")
    public ResponseEntity<ResponseResult> forgotPassword(@RequestParam("email") String email) {
        Optional<User> userOptional = userRepository.findByEmail(email);
        if (userOptional.isEmpty()) {

            return ResponseEntity.status(HttpStatus.OK).body(
                    new ResponseResult("ok", "User with email" + email + "not found", email, 1)
            );
        }

        User user = userOptional.get();
        service.generateResetToken(user);

//        String resetUrl = "http://localhost:8080/reset-password?token=" + user.getResetToken();
        String message = "Su dung ma de thay doi mat khau: " + user.getResetToken();

        emailService.sendEmail(user.getEmail(), "Change password", message);

        return ResponseEntity.status(HttpStatus.OK).body(
                new ResponseResult("ok", "Ma token da duoc guir ve email" + user.getEmail(), user.getResetToken(), 1)
        );
    }

    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestParam("token") String resetToken, @RequestParam("password") String newPassword) {
        Token token = tokenRepository.findByValue(resetToken);
        if (token == null) {
            return ResponseEntity.badRequest().body("Invalid reset token");
        }

        User user = userRepository.findByUsername(token.getUsername()).orElse(null);
        if (user == null) {
            return ResponseEntity.badRequest().body("User not found");
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        tokenRepository.delete(token);


        return ResponseEntity.ok().body("Password reset successfully");
    }

    @GetMapping("/byRole/{role}")
    public ResponseEntity<ResponseResult> getUsersByRole(@PathVariable Role role) {
        return service.getUsersByRole(role);
    }

    @PutMapping("/customer/{id}")
    ResponseEntity<ResponseResult> updateUser(@RequestBody RegisterRequest request, @PathVariable Long id) {
        return service.updateUser(id, request);
    }

    @PutMapping("/customer/{id}/password")
    ResponseEntity<ResponseResult> ChangePassword(@RequestBody RegisterRequest request, @PathVariable Long id) {
        return service.updatePassword(id, request);
    }

    @GetMapping("/{id}")
        //Let's return an object with: data, message, status
    ResponseEntity<ResponseResult> findById(@PathVariable Long id) {
        return service.findById(id);
    }


}
