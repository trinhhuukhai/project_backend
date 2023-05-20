package com.project.controller;

import com.project.dto.request.OrderRequest;
import com.project.dto.request.ProductRequest;
import com.project.dto.response.OrderResponse;
import com.project.model.Order;
import com.project.model.Product;
import com.project.repository.ProductRepository;
import com.project.response.ResponseResult;
import com.project.service.OrderItemService;
import com.project.service.OrderService;
import com.project.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/api/v1/order")
@CrossOrigin("http://localhost:3000")
public class OrderController {
    @Autowired
    private OrderService orderService;

    @Autowired
    private OrderItemService orderItemService;

    @GetMapping("/getOrder")
    ResponseEntity<ResponseResult> getAll(){
        return orderService.getAllOrder();
    }

    @GetMapping("/{id}")
        //Let's return an object with: data, message, status
    ResponseEntity<ResponseResult> findById(@PathVariable Long id) {
        return orderService.findById(id);
    }

    @PostMapping("/insert")
    ResponseEntity<ResponseResult> insertOrder(@RequestBody OrderRequest newOrder) {
        return orderService.insertOrder(newOrder);

    }

    @GetMapping("/{id}/orderItem")
    ResponseEntity<ResponseResult> findByOrderId(@PathVariable Long id) {
        return orderItemService.findOrderItemsByOrderId(id);
    }

    @PutMapping("/{id}")
    ResponseEntity<ResponseResult> updateOrder(@RequestBody OrderRequest newOrder, @PathVariable Long id) {
        return orderService.updateOrder(newOrder,id);
    }

    @PutMapping("/{id}/status")
    ResponseEntity<ResponseResult> updateStatus(@RequestParam("status") String status, @PathVariable Long id) {
        return orderService.CancelOrder(status, id);
    }


    @DeleteMapping("/{id}")
    ResponseEntity<ResponseResult> deleteOrder(@PathVariable Long id) {
        return orderService.deleteOrder(id);
    }

    @GetMapping("/{id}/item")
    public ResponseEntity<ResponseResult> getOrderById(@PathVariable Long id) {
        return orderService.findByIds(id);
    }

    @GetMapping("/date")
    public ResponseEntity<List<Order>> getOrdersByDate(
            @RequestParam("date") @DateTimeFormat(pattern = "yyyy-MM-dd") Date date){
        List<Order> orders = orderService.findOrderByDate(date);
        return new ResponseEntity<>(orders, HttpStatus.OK);
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<Order>> getOrdersByStatus(@PathVariable String status) {
        List<Order> orders = orderService.getOrdersByStatus(status);
        return new ResponseEntity<>(orders, HttpStatus.OK);
    }
    @GetMapping("/payment/{status}")
    public ResponseEntity<List<Order>> getOrdersByPayment(@PathVariable String status) {
        List<Order> orders = orderService.getOrdersByPaymentStatus(status);
        return new ResponseEntity<>(orders, HttpStatus.OK);
    }

    @GetMapping("/rangeDate")
    public ResponseEntity<List<Order>> getOrdersByDateRange(@RequestParam("startDate") @DateTimeFormat(pattern = "yyyy-MM-dd") Date startDate, @RequestParam("endDate") @DateTimeFormat(pattern = "yyyy-MM-dd") Date endDate) {
        List<Order> orders = orderService.findOrdersByDateRange(startDate, endDate);
        return new ResponseEntity<>(orders, HttpStatus.OK);
    }

    @GetMapping("/week")
    public ResponseEntity<List<Order>> getOrdersForCurrentWeek() {
        List<Order> orders = orderService.findOrdersForCurrentWeek();
        return new ResponseEntity<>(orders, HttpStatus.OK);
    }

    @GetMapping("/month")
    public ResponseEntity<List<Order>> getOrdersForCurrentMonth() {
        List<Order> orders = orderService.findOrdersForCurrentMonth();
        return new ResponseEntity<>(orders, HttpStatus.OK);
    }

    @GetMapping("/yesterday")
    public ResponseEntity<List<Order>> getOrdersFromYesterday() {
        List<Order> orders = orderService.findOrdersFromYesterday();
        return new ResponseEntity<>(orders, HttpStatus.OK);
    }

    @GetMapping("/previousMonth")
    public ResponseEntity<List<Order>> getOrdersForPreviousMonth() {
        List<Order> orders = orderService.findOrdersForPreviousMonth();
        return new ResponseEntity<>(orders, HttpStatus.OK);
    }

    @GetMapping("/lastWeek")
    public ResponseEntity<List<Order>> getOrdersForLastWeek() {
        List<Order> orders = orderService.findOrdersForLastWeek();
        return new ResponseEntity<>(orders, HttpStatus.OK);
    }
}
