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
    ResponseEntity<ResponseResult> insertPayment(@RequestBody PaymentRequest newPay) {
        return paymentService.addPayment(newPay);

    }
    @GetMapping("/count")
    public ResponseEntity<Long> countPaymentAmount() {
        Long count = paymentService.countPaymentAmount();
        return ResponseEntity.ok(count);
    }

    @GetMapping("/today")
    public List<Payment> getPaymentMadeToday() {
        return paymentService.getPaymentsMadeToday();
    }

    @GetMapping("/date")
    public ResponseEntity<List<Payment>> getPaymentByDate(
            @RequestParam("date") @DateTimeFormat(pattern = "yyyy-MM-dd") Date date){
        List<Payment> payments = paymentService.findPaymentByDate(date);
        return new ResponseEntity<>(payments, HttpStatus.OK);
    }

    @GetMapping("/rangeDate")
    public ResponseEntity<List<Payment>> getPaymentByDateRange(@RequestParam("startDate") @DateTimeFormat(pattern = "yyyy-MM-dd") Date startDate, @RequestParam("endDate") @DateTimeFormat(pattern = "yyyy-MM-dd") Date endDate) {
        List<Payment> payments = paymentService.findPaymentByDateRange(startDate, endDate);
        return new ResponseEntity<>(payments, HttpStatus.OK);
    }

    @GetMapping("/week")
    public ResponseEntity<List<Payment>> getPaymentForCurrentWeek() {
        List<Payment> payments = paymentService.findPaymentForCurrentWeek();
        return new ResponseEntity<>(payments, HttpStatus.OK);
    }

    @GetMapping("/month")
    public ResponseEntity<List<Payment>> getPaymentForCurrentMonth() {
        List<Payment> payments = paymentService.findPaymentForCurrentMonth();
        return new ResponseEntity<>(payments, HttpStatus.OK);
    }

    @GetMapping("/yesterday")
    public ResponseEntity<List<Payment>> getPaymentFromYesterday() {
        List<Payment> payments = paymentService.findPaymentFromYesterday();
        return new ResponseEntity<>(payments, HttpStatus.OK);
    }

    @GetMapping("/previousMonth")
    public ResponseEntity<List<Payment>> getPaymentForPreviousMonth() {
        List<Payment> payments = paymentService.findPaymentForPreviousMonth();
        return new ResponseEntity<>(payments, HttpStatus.OK);
    }

    @GetMapping("/lastWeek")
    public ResponseEntity<List<Payment>> getPaymentForLastWeek() {
        List<Payment> payments = paymentService.findPaymentForLastWeek();
        return new ResponseEntity<>(payments, HttpStatus.OK);
    }

//    @PutMapping("/{id}")
//    ResponseEntity<ResponseResult> updatePayment(@RequestBody  Payment newPay, @PathVariable Long id) {
//        return paymentService.updatePayment(newPay,id);
//    }
//
//    @DeleteMapping("/{id}")
//    ResponseEntity<ResponseResult> deletePayment(@PathVariable Long id) {
//        return paymentService.deletePay(id);
//    }
}
