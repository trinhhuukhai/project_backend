package com.project.controller;


import com.project.repository.OrderItemRepository;
import com.project.response.ResponseResult;
import com.project.service.CustomerService;
import com.project.service.OrderItemService;
import com.project.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/shop")
@RequiredArgsConstructor
@CrossOrigin("http://localhost:3000")
public class ShopController {

    @Autowired
    private ProductService productService;

    @Autowired
    private OrderItemService orderItemService;

    @Autowired
    private CustomerService customerService;

    @GetMapping("/{id}/product")
    ResponseEntity<ResponseResult> findProduct(@PathVariable Long id) {
        return productService.findProductByShopId(id);
    }

    @GetMapping("/{id}/orderItem")
    ResponseEntity<ResponseResult> findOrder(@PathVariable Long id) {
        return orderItemService.findOrderItemsByShopId(id);
    }

    @GetMapping("/{id}/payment")
    ResponseEntity<ResponseResult> findOrderPayment(@PathVariable Long id) {
        return orderItemService.findPayment(id);
    }

    @GetMapping("/{id}/total-by-day")
    public ResponseEntity<Map<Date, Double>> getTotalSalesByDay(@PathVariable Long id) {
        Map<Date, Double> totalSalesByDay = orderItemService.getTotalSalesByDay(id);
        return ResponseEntity.ok(totalSalesByDay);
    }

    @GetMapping("/{id}/customer")
    ResponseEntity<ResponseResult> findCustomer(@PathVariable Long id) {
        return customerService.findCustomerByShopId(id);
    }

}
