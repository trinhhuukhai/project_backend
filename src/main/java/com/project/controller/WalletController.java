package com.project.controller;

import com.project.dto.request.OrderRequest;
import com.project.dto.request.WalletRequest;
import com.project.model.Order;
import com.project.model.Wallet;
import com.project.response.ResponseResult;
import com.project.service.OrderItemService;
import com.project.service.OrderService;
import com.project.service.WalletService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/api/v1/wallet")
@CrossOrigin("http://localhost:3000")
public class WalletController {
    @Autowired
    private WalletService walletService;

    @Autowired
    private OrderItemService orderItemService;

    @GetMapping("/getWallet")
    List<Wallet> getAll(){
        return (List<Wallet>) walletService.getAllWallet();
    }


    @PostMapping("/insert")
    ResponseEntity<ResponseResult> insertWallet(@RequestBody WalletRequest request) {
        return walletService.addWallet(request);

    }

    @GetMapping("/findByUser/{id}")
    public Wallet findbyuser(@RequestBody Long id) {
        return walletService.find(id);

    }

    @PostMapping("/{walletId}/top-up")
    public ResponseEntity<ResponseResult> topUp(@PathVariable Long walletId, @RequestParam Double balance) {
       return  walletService.topUp(walletId,balance);
    }
}
