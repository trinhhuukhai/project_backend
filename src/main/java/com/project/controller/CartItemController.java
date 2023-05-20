package com.project.controller;

import com.project.dto.request.CartItemRequest;
import com.project.dto.response.CartResponse;
import com.project.model.CartItem;
import com.project.model.Product;
import com.project.response.ResponseResult;
import com.project.service.CartItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/cartItem")
@CrossOrigin("http://localhost:3000")
public class CartItemController {

    @Autowired
    private CartItemService cartItemService;
    @GetMapping("/getAllCartItem")
    List<CartItem> getAll(){
        return (List<CartItem>) cartItemService.getAllCart();
    }

    @PostMapping("/insert")
    ResponseEntity<ResponseResult> insertProduct(@RequestBody CartItemRequest newCart) {
        return cartItemService.insertCartItem(newCart);

    }

    @PutMapping("/{cartItemId}")
    public ResponseEntity<ResponseResult> updateCartItemQuantity(
            @PathVariable Long cartItemId,
            @RequestBody Map<String, Integer> request) {

        Integer newQuantity = request.get("newQuantity");

        return cartItemService.updateCartItemQuantity(cartItemId, newQuantity);
    }

    @DeleteMapping("/{id}")
    ResponseEntity<ResponseResult> deleteCart(@PathVariable Long id) {
        return cartItemService.deleteCartItem(id);
    }

    @GetMapping("/user/{id}")
    public ResponseEntity <ResponseResult> getCartByUser(@PathVariable Long id) {
        return cartItemService.findByUserId(id);
    }

    @GetMapping("/{id}")
    public ResponseEntity <ResponseResult> getById(@PathVariable Long id) {
        return cartItemService.findById(id);
    }

}
