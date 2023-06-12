package com.project.controller;

import com.project.dto.request.CartItemRequest;
import com.project.dto.response.CartResponse;
import com.project.model.CartItem;
import com.project.model.Product;
import com.project.response.ResponseResult;
import com.project.service.CartItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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
    @PreAuthorize("hasAuthority('CUSTOMER')")
    List<CartItem> getAll(){
        return (List<CartItem>) cartItemService.getAllCart();
    }

    @PostMapping("/insert")
    @PreAuthorize("hasAuthority('CUSTOMER')")
    ResponseEntity<ResponseResult> insertProduct(@RequestBody CartItemRequest newCart) {
        return cartItemService.insertCartItem(newCart);

    }

    @PutMapping("/{cartItemId}")
    @PreAuthorize("hasAuthority('CUSTOMER')")
    public ResponseEntity<ResponseResult> updateCartItemQuantity(@RequestBody CartItemRequest cartItemRequest,@PathVariable Long cartItemId ) {

        return cartItemService.updateCartItem(cartItemRequest, cartItemId);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('CUSTOMER')")
    ResponseEntity<ResponseResult> deleteCart(@PathVariable Long id) {
        return cartItemService.deleteCartItem(id);
    }

    @GetMapping("/user/{id}")
    @PreAuthorize("hasAuthority('CUSTOMER')")
    public ResponseEntity <ResponseResult> getCartByUser(@PathVariable Long id) {
        return cartItemService.findByUserId(id);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('CUSTOMER')")
    public ResponseEntity <ResponseResult> getById(@PathVariable Long id) {
        return cartItemService.findById(id);
    }

}
