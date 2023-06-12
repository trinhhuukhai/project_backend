package com.project.service;

import com.project.dto.request.CartItemRequest;
import com.project.dto.response.CartResponse;
import com.project.dto.response.ProductResponse;
import com.project.model.CartItem;
import com.project.model.Product;
import com.project.model.Shop;
import com.project.model.User;
import com.project.repository.CartItemRepository;
import com.project.repository.ProductRepository;
import com.project.repository.ShopRepository;
import com.project.repository.UserRepository;
import com.project.response.ResponseResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CartItemService {
    @Autowired
    private CartItemRepository cartItemRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ShopRepository shopRepository;


    @Autowired
    UserRepository userRepository;

    public List<CartItem> getAllCart(){
        return cartItemRepository.findAll();
    }


    public ResponseEntity<ResponseResult> insertCartItem(CartItemRequest newCartItem) {
        Product product = productRepository.findById(newCartItem.getProductId()).orElseThrow();
        Shop shop = product.getShop();
        User user = userRepository.findById(newCartItem.getUserId()).orElseThrow();

        int orderedQuantity = newCartItem.getQuantity() != null && newCartItem.getQuantity() > 0 ? newCartItem.getQuantity() : 1;

        if (orderedQuantity > product.getInventory()) {
            return ResponseEntity.status(HttpStatus.OK).body(
                    new ResponseResult("error", "Số lượng sản phẩm không đủ!!", null, 0)
            );
        }

        List<CartItem> cartItems = cartItemRepository.findByUser(user);
        CartItem existingCartItem = null;
        for (CartItem cartItem : cartItems) {
            if (cartItem.getProduct().getId().equals(newCartItem.getProductId()) && cartItem.getSize().equals(newCartItem.getSize())) {
                existingCartItem = cartItem; break;
            }
        }
        if (existingCartItem == null) {
            CartItem cartItem = new CartItem();
            cartItem.setUser(user);
            cartItem.setProduct(product);
            cartItem.setQuantity(orderedQuantity);
            cartItem.setPrice(product.getOutputPrice());
            cartItem.setTotalPrice(orderedQuantity * product.getOutputPrice());
            cartItem.setSize(newCartItem.getSize());
            cartItemRepository.save(cartItem);
            product.setInventory(product.getInventory() - orderedQuantity);
            productRepository.save(product);
            return ResponseEntity.status(HttpStatus.OK).body(
                    new ResponseResult("ok", "Thêm vào giỏ hàng thành công", cartItem, 1)
            );
        } else {
            int totalQuantity = existingCartItem.getQuantity() + orderedQuantity;
            if (totalQuantity > product.getInventory()) {
                return ResponseEntity.status(HttpStatus.OK).body(
                        new ResponseResult("error", "Số lượng sản phẩm không đủ", null, 0)
                );
            }
            Double newTotalPrice = existingCartItem.getProduct().getOutputPrice() * totalQuantity;
            product.setInventory(product.getInventory() - orderedQuantity);
            productRepository.save(product);
            existingCartItem.setQuantity(totalQuantity);
            existingCartItem.setTotalPrice(newTotalPrice);
            cartItemRepository.save(existingCartItem);
            return ResponseEntity.status(HttpStatus.OK).body(
                    new ResponseResult("ok", "Thêm sản phẩm vào giỏ hàng thành công", existingCartItem, 1)
            );
        }
    }

    public ResponseEntity<ResponseResult> updateCartItem(CartItemRequest newCartItem, Long id) {
        CartItem cartItem = cartItemRepository.findById(id).orElseThrow();

        Product product = cartItem.getProduct();
        Integer oldQuantity = cartItem.getQuantity();
        String oldSize = cartItem.getSize();

        if (newCartItem.getQuantity() != null) {
            if (newCartItem.getQuantity() <= product.getInventory()) {
                cartItem.setQuantity(newCartItem.getQuantity());
                cartItem.setTotalPrice(cartItem.getPrice() * cartItem.getQuantity());

                // Update product inventory
                int diffQuantity = cartItem.getQuantity() - oldQuantity;
                product.setInventory(product.getInventory() - diffQuantity);
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                        new ResponseResult("error", "Quantity in cart cannot be greater than the quantity in the product", null, 0)
                );
            }
        }

        if (newCartItem.getSize() != null) {
            cartItem.setSize(newCartItem.getSize());
        }

        CartItem updatedCartItem = cartItemRepository.save(cartItem);
        productRepository.save(product);

        return ResponseEntity.status(HttpStatus.OK).body(
                new ResponseResult("ok", "Cart item updated successfully", updatedCartItem, 1)
        );
    }


    //Delete a Product => DELETE method
    public ResponseEntity<ResponseResult> deleteCartItem(Long cartItemId) {
        CartItem cartItem = cartItemRepository.findById(cartItemId).orElse(null);
        if (cartItem == null) {
            return ResponseEntity.status(HttpStatus.OK).body(
                    new ResponseResult("error", "Cart item not found", null, 0)
            );
        }

        // Update product inventory
        Product product = cartItem.getProduct();
        product.setInventory(product.getInventory() + cartItem.getQuantity());
        productRepository.save(product);

        cartItemRepository.delete(cartItem);

        return ResponseEntity.status(HttpStatus.OK).body(
                new ResponseResult("ok", "Cart item deleted successfully", null, 1)
        );
    }


    //Let's return an object with: data, message, status
    public ResponseEntity<ResponseResult> findById(Long id) {
        Optional<CartItem> foundCartItem = cartItemRepository.findById(id);
        return foundCartItem.isPresent() ?
                ResponseEntity.status(HttpStatus.OK).body(
                        new ResponseResult("ok", "Query Customer successfully", foundCartItem,1)
                        //you can replace "ok" with your defined "error code"
                ):
                ResponseEntity.status(HttpStatus.OK).body(
                        new ResponseResult("failed", "Cannot find Customer with id = "+id, "",1)
                );
    }

    public ResponseEntity<ResponseResult> findByUserId(Long id) {
        List<CartItem> foundCartItem = cartItemRepository.findByUserId(id);
        return !foundCartItem.isEmpty() ?
                ResponseEntity.status(HttpStatus.OK).body(
                        new ResponseResult("ok", "Query Customer successfully", foundCartItem, foundCartItem.size())
                        //you can replace "ok" with your defined "error code"
                ):
                ResponseEntity.status(HttpStatus.OK).body(
                        new ResponseResult("failed", "Cannot find Customer with id = "+id, "",1)
                );
    }


}
