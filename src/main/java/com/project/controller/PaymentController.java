package com.project.controller;

import com.project.dto.request.PaymentRequest;
import com.project.dto.request.ProductRequest;
import com.project.model.Order;
import com.project.model.Payment;
import com.project.model.Product;
import com.project.repository.ProductRepository;
import com.project.response.ResponseResult;
import com.project.service.PaymentService;
import com.project.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/api/v1/payment")
@CrossOrigin("http://localhost:3000")
public class PaymentController {
    @Autowired
    private PaymentService paymentService;


    @GetMapping("/getAllPayment")

    List<Payment> getAll(){
        return (List<Payment>) paymentService.getAllPayment();
    }

//    @GetMapping("/{id}")
//        //Let's return an object with: data, message, status
//    ResponseEntity<ResponseResult> findById(@PathVariable Long id) {
//        return paymentService.findById(id);
//    }

    @PostMapping("/insert")
    @PreAuthorize("hasAuthority('CUSTOMER')")
    ResponseEntity<ResponseResult> insertPayment(@RequestBody PaymentRequest newPay) {
        return paymentService.addPayment(newPay);

    }


}
