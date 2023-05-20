package com.project.controller;


import com.project.dto.request.OrderFromCart;
import com.project.dto.request.OrderItemRequest;
import com.project.dto.request.ProductRequest;
import com.project.dto.response.AllResponse;
import com.project.model.OrderItem;
import com.project.model.Payment;
import com.project.model.Product;
import com.project.repository.ProductRepository;
import com.project.response.ResponseResult;
import com.project.service.OrderItemService;
import com.project.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/api/v1/orderItem")
@CrossOrigin("http://localhost:3000")
public class OrderItemController {
    @Autowired
    private OrderItemService orderItemService;


    @GetMapping("/getAllOrderItem")
    List<OrderItem> getAll(){
        return (List<OrderItem>) orderItemService.getAllOrderItem();
    }

    @GetMapping("/count")
    public ResponseEntity<Long> countTotal() {
        Long count = orderItemService.countTotal();
        return ResponseEntity.ok(count);
    }

    @GetMapping("/{id}")
        //Let's return an object with: data, message, status
    ResponseEntity<ResponseResult> findById(@PathVariable Long id) {
        return orderItemService.findById(id);
    }

    @PostMapping("/insertFromCart")
    ResponseEntity<ResponseResult> insertOrderFromCart(@RequestBody OrderFromCart newItem) {
        return orderItemService.insertOrderItemFromCart(newItem);

    }

    @GetMapping("/today")
    public List<OrderItem> getPaymentMadeToday(
            @RequestParam("shopId") Long shopId
    ) {
        return orderItemService.getOrderItemToday(shopId);
    }

    @GetMapping("/date")
    public ResponseEntity<List<OrderItem>> getPaymentByDate(
            @RequestParam("date") @DateTimeFormat(pattern = "yyyy-MM-dd") Date date,  @RequestParam("shopId") Long shopId){
        List<OrderItem> orderItemList = orderItemService.findOrderItemByDate(date, shopId);
        return new ResponseEntity<>(orderItemList, HttpStatus.OK);
    }

    @GetMapping("/rangeDate")
    public ResponseEntity<List<OrderItem>> getPaymentByDateRange(@RequestParam("startDate") @DateTimeFormat(pattern = "yyyy-MM-dd") Date startDate, @RequestParam("endDate") @DateTimeFormat(pattern = "yyyy-MM-dd") Date endDate, @RequestParam("shopId") Long shopId) {
        List<OrderItem> orderItemList = orderItemService.findOrderItemByDateRange(startDate, endDate, shopId);
        return new ResponseEntity<>(orderItemList, HttpStatus.OK);
    }

    @GetMapping("/week")
    public ResponseEntity<List<OrderItem>> getPaymentForCurrentWeek( @RequestParam("shopId") Long shopId) {
        List<OrderItem> orderItemList = orderItemService.findOrderItemForCurrentWeek(shopId);
        return new ResponseEntity<>(orderItemList, HttpStatus.OK);
    }

    @GetMapping("/month")
    public ResponseEntity<List<OrderItem>> getPaymentForCurrentMonth( @RequestParam("shopId") Long shopId) {
        List<OrderItem> orderItemList = orderItemService.findOrderItemForCurrentMonth(shopId);
        return new ResponseEntity<>(orderItemList, HttpStatus.OK);
    }

    @GetMapping("/yesterday")
    public ResponseEntity<List<OrderItem>> getPaymentFromYesterday( @RequestParam("shopId") Long shopId) {
        List<OrderItem> orderItemList = orderItemService.findOrderItemFromYesterday(shopId);
        return new ResponseEntity<>(orderItemList, HttpStatus.OK);
    }

    @GetMapping("/previousMonth")
    public ResponseEntity<List<OrderItem>> getPaymentForPreviousMonth( @RequestParam("shopId") Long shopId) {
        List<OrderItem> orderItemList = orderItemService.findOrderItemForPreviousMonth(shopId);
        return new ResponseEntity<>(orderItemList, HttpStatus.OK);
    }

    @GetMapping("/lastWeek")
    public ResponseEntity<List<OrderItem>> getPaymentForLastWeek( @RequestParam("shopId") Long shopId) {
        List<OrderItem> orderItemList = orderItemService.findOrderItemForLastWeek(shopId);
        return new ResponseEntity<>(orderItemList, HttpStatus.OK);
    }

    @PostMapping("/insert")
    ResponseEntity<ResponseResult> insertOrderItem(@RequestBody OrderItemRequest newItem) {
        return orderItemService.insertOrderItem(newItem);
    }

    @PutMapping("/{id}/refund")
    ResponseEntity<ResponseResult> updateStatusPayment(@RequestParam("status") String status, @PathVariable Long id) {
        return orderItemService.RefundPayment(status, id);
    }

    @PutMapping("/{id}")
    ResponseEntity<ResponseResult> updateOrderItem(@RequestBody OrderItemRequest newItem, @PathVariable Long id) {
        return orderItemService.updateOrderItem(newItem,id);
    }

    @DeleteMapping("/{id}")
    ResponseEntity<ResponseResult> deleteOrderItem(@PathVariable Long id) {
        return orderItemService.deleteOrderItem(id);
    }
}
