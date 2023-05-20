package com.project.service;

import com.project.dto.request.RegisterRequest;
import com.project.model.*;
import com.project.repository.ShopRepository;
import com.project.repository.TokenRepository;
import com.project.repository.UserRepository;
import com.project.dto.request.AuthRequest;
import com.project.dto.response.AuthResponse;
import com.project.repository.WalletRepository;
import com.project.response.ResponseResult;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    private  final  JwtService jwtService;

    @Autowired private WalletRepository walletRepository;

    @Autowired
    private TokenRepository tokenRepository;

    @Autowired
    private ShopRepository shopRepository;

    @Autowired
    private ProductService productService;


    @Autowired
    private EmailService emailService;
    private final TokenService tokenService;

    private  final AuthenticationManager authenticationManager;
    public ResponseEntity<ResponseResult> register(RegisterRequest request) {
        var user = User.builder()
                .name(request.getName())
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .address(request.getAddress())
                .role(request.getRole())
                .phone(request.getPhone())
                .email(request.getEmail())
                .build();

        Optional<User> nameUser = userRepository.findByUsername(user.getUsername());
        Optional<User> emailUser = userRepository.findByEmail(user.getEmail());

        if (nameUser.isPresent()) {
            return ResponseEntity.status(HttpStatus.OK).body(
                    new ResponseResult("failed", "User name đã tồn tại", "", 1)
            );
        } else if (emailUser.isPresent()) {
            return ResponseEntity.status(HttpStatus.OK).body(
                    new ResponseResult("failed", "Email đã được đăng ký", "", 1)
            );
        } else {
            if (request.getRole() == Role.CUSTOMER) {
                Wallet wallet = new Wallet();
                wallet.setBalance(0.0);

                walletRepository.save(wallet);
                user.setWallet(wallet);
                user.setShop(null); // Set the shop to null for customers
            } else if (request.getRole() == Role.OWNER) {
                Shop shop = new Shop();
                shop.setName("Shop quần áo " + user.getName());
                shop.setDescription("Shop thời trang");
                shopRepository.save(shop);
                user.setShop(shop);
                user.setWallet(null); // Set the wallet to null for owners
            }

            userRepository.save(user);
            var jwtToken = jwtService.generateToken(user);
            // emailService.sendEmail(user.getEmail(),
            //         "Account registered successfully with username: " + user.getUsername(),
            //         "Welcome to our platform!");

            return ResponseEntity.status(HttpStatus.OK).body(
                    new ResponseResult("Successfull", "dang ky tai khoan thanh cong", user.getUsername(), 1)
            );
        }
    }




    public ResponseEntity<ResponseResult> updateUser( Long id, RegisterRequest request) {

        Optional<User> optionalUser = userRepository.findById(id);
        if (optionalUser.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    new ResponseResult("failed", "User not found", "", 1)
            );
        }

        User user = optionalUser.get();
        user.setName(request.getName() != null ? request.getName() : user.getName());
        user.setAddress(request.getAddress() != null ? request.getAddress() : user.getAddress());
        user.setPhone(request.getPhone() != null ? request.getPhone() : user.getPhone());
        user.setEmail(request.getEmail() != null ? request.getEmail() : user.getEmail());


        userRepository.save(user);

        return ResponseEntity.status(HttpStatus.OK).body(
                new ResponseResult("success", "User updated successfully", user.getUsername(), 1)
        );
    }


    public ResponseEntity<ResponseResult> updatePassword( Long id, RegisterRequest request) {

        Optional<User> optionalUser = userRepository.findById(id);

        if (optionalUser.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    new ResponseResult("failed", "User not found", "", 1)
            );
        }

        User user = optionalUser.get();

        user.setPassword(request.getPassword() != null ? passwordEncoder.encode(request.getPassword()) : user.getPassword());

        userRepository.save(user);

        return ResponseEntity.status(HttpStatus.OK).body(
                new ResponseResult("success", "User updated successfully", user.getUsername(), 1)
        );
    }



    public ResponseEntity<ResponseResult> login(AuthRequest request){
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getUsername(),
                            request.getPassword()
                    )
            );
        } catch (AuthenticationException e) {
            // Handle authentication failure here
            return ResponseEntity.status(HttpStatus.OK).body(
                    new ResponseResult("failed", "Login false", "", 1)
            );        }
        var user = userRepository.findByUsername(request.getUsername()).orElseThrow();
        var jwtToken = jwtService.generateToken(user);
        List<Token> listOldToken = tokenService.findAllByUsername(user.getUsername());
        for (Token token: listOldToken) {
            tokenService.deleteToken(token.getValue());
        }
        Token tokenObj = new Token();
        tokenObj.setUsername(user.getUsername());
        tokenObj.setUserId(user.getId());
        tokenObj.setValue(jwtToken);
        tokenService.save(tokenObj);
        return ResponseEntity.status(HttpStatus.OK).body( AuthResponse.builder()
                .token(jwtToken)
                .id(user.getId())
                .roleName(String.valueOf(user.getRole()))
                .name(user.getName())
                .username(user.getUsername())
                .email(user.getEmail())
                .phone(user.getPhone())
                .address(user.getAddress())
                .shopId(user.getShop() != null ? user.getShop().getId() : 0L)
                .walletId(user.getWallet() != null ? user.getWallet().getId() : 0L)
                .build());
    }


    public List<User> getAllUser(){
        return userRepository.findAll();
    }



    public void generateResetToken(User user) {
        String resetToken = UUID.randomUUID().toString();
        user.setResetToken(resetToken);
        userRepository.save(user);

        Token token = new Token();
        token.setUserId(user.getId());
        token.setUsername(user.getUsername());
        token.setValue(resetToken);
        tokenRepository.save(token);
    }

    public  ResponseEntity<ResponseResult> getUsersByRole(Role role) {
        List<User> findList = userRepository.findByRole(role);
        return !findList.isEmpty() ?
                ResponseEntity.status(HttpStatus.OK).body(
                        new ResponseResult("ok", "list customer", findList,findList.size())
                        //you can replace "ok" with your defined "error code"
                ):
                ResponseEntity.status(HttpStatus.OK).body(
                        new ResponseResult("failed", "np list ", "",1)
                );
    }

    public ResponseEntity<ResponseResult> findById(@PathVariable Long id) {
        Optional<User> foundUser = userRepository.findById(id);
        return foundUser.isPresent() ?
                ResponseEntity.status(HttpStatus.OK).body(
                        new ResponseResult("ok", "Query category successfully", foundUser,1)
                        //you can replace "ok" with your defined "error code"
                ):
                ResponseEntity.status(HttpStatus.OK).body(
                        new ResponseResult("failed", "Cannot find category with id = "+id, "",1)
                );
    }

    @GetMapping("/{id}/product")
    ResponseEntity<ResponseResult> findByCategoryId(@PathVariable Long id) {
        return productService.findProductByCategoryId(id);
    }

}
