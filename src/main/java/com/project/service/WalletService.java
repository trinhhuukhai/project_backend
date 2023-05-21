package com.project.service;

import com.project.dto.request.WalletRequest;
import com.project.model.Category;
import com.project.model.User;
import com.project.model.Wallet;
import com.project.repository.PaymentRepository;
import com.project.repository.UserRepository;
import com.project.repository.WalletRepository;
import com.project.response.ResponseResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;
import java.util.Optional;

@Service
public class WalletService {
    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private WalletRepository walletRepository;

    public List<Wallet> getAllWallet(){
        return walletRepository.findAll();
    }

//    public ResponseEntity<ResponseResult> insertPay(Payment newPay) {
//
//        return ResponseEntity.status(HttpStatus.OK).body(
//                new ResponseResult("ok", "Insert Customer successfully", paymentRepository.save(newPay),1)
//        );
//    }

    public Wallet find(Long id){
        return walletRepository.findByUserId(id);
    }

    public ResponseEntity<ResponseResult> findById(@PathVariable Long id) {
        Optional<Wallet> foundCategory = walletRepository.findById(id);
        return foundCategory.isPresent() ?
                ResponseEntity.status(HttpStatus.OK).body(
                        new ResponseResult("ok", "Query category successfully", foundCategory,1)
                        //you can replace "ok" with your defined "error code"
                ):
                ResponseEntity.status(HttpStatus.OK).body(
                        new ResponseResult("failed", "Cannot find category with id = "+id, "",1)
                );
    }

    public ResponseEntity<ResponseResult> addWallet(WalletRequest request) {
        // Get the order
        User user = userRepository.findById(request.getUserId()).orElseThrow();
        // Check if the user already has a wallet
        if (user.getWallet() != null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    new ResponseResult("error", "User already has a wallet", null, 0)
            );
        }

        // Create a new wallet
        Wallet wallet = new Wallet();
        wallet.setBalance(request.getBalance());
        wallet.setUser(user);

        // Save the wallet
        walletRepository.save(wallet);

        // Update the user's wallet reference
        user.setWallet(wallet);
        userRepository.save(user);

        // Return a success response
        return ResponseEntity.status(HttpStatus.OK).body(
                new ResponseResult("ok", "Wallet created successfully", wallet, 1)
        );
    }

    public ResponseEntity<ResponseResult> topUp(Long walletId, Double balance) {
        Optional<Wallet> walletOptional = walletRepository.findById(walletId);
        if (walletOptional.isPresent()) {
            Wallet wallet = walletOptional.get();
            wallet.topUp(balance);

            return ResponseEntity.status(HttpStatus.OK).body(
                    new ResponseResult("ok", "Wallet created successfully",   walletRepository.save(wallet), 1)
            );
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    new ResponseResult("error", "no find wallet", null, 0)
            );
        }
    }

}
