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
                    new ResponseResult("error", "Ordered quantity exceeds available inventory", null, 0)
            );
        }

        // Retrieve all cart items for the user
        List<CartItem> cartItems = cartItemRepository.findByUser(user);

        // Check if the product already exists in the user's cart
        CartItem existingCartItem = null;
        for (CartItem cartItem : cartItems) {
            if (cartItem.getProduct().getId().equals(newCartItem.getProductId())) {
                existingCartItem = cartItem;
                break;
            }
        }

        if (existingCartItem == null) {
            // Product not in cart, add new cart item
            CartItem cartItem = new CartItem();
            cartItem.setUser(user);
            cartItem.setProduct(product);
            cartItem.setQuantity(orderedQuantity);
            cartItem.setPrice(product.getOutputPrice());
            cartItem.setTotalPrice(orderedQuantity * product.getOutputPrice());
//            cartItem.setShop(shop);
            cartItemRepository.save(cartItem);

            // Reduce product quantity
            product.setInventory(product.getInventory() - orderedQuantity);
            productRepository.save(product);

            return ResponseEntity.status(HttpStatus.OK).body(
                    new ResponseResult("ok", "Insert Product successfully",cartItem,1)
            );
        } else {
            // Product already in cart, update quantity
            int totalQuantity = existingCartItem.getQuantity() + orderedQuantity;
            if (totalQuantity > product.getInventory()) {
                return ResponseEntity.status(HttpStatus.OK).body(
                        new ResponseResult("error", "Ordered quantity exceeds available inventory", null, 0)
                );
            }

            Double newTotalPrice = existingCartItem.getProduct().getOutputPrice() * totalQuantity;
            // Reduce product quantity
            product.setInventory(product.getInventory() - orderedQuantity);
            productRepository.save(product);

            existingCartItem.setQuantity(totalQuantity);
            existingCartItem.setTotalPrice(newTotalPrice);
            cartItemRepository.save(existingCartItem);

            return ResponseEntity.status(HttpStatus.OK).body(
                    new ResponseResult("ok", "Update Product successfully",existingCartItem,1)
            );
        }
    }







    public ResponseEntity<ResponseResult> updateCartItemQuantity(Long cartItemId, Integer newQuantity) {

        CartItem cartItem = cartItemRepository.findById(cartItemId).orElseThrow();
        Product product = cartItem.getProduct();

        if (newQuantity <= product.getInventory()) {
            int oldQuantity = cartItem.getQuantity();
            cartItem.setQuantity(newQuantity);

            CartItem updatedCartItem = cartItemRepository.save(cartItem);

            // Update product inventory
            int diffQuantity = newQuantity - oldQuantity;
            product.setInventory(product.getInventory() - diffQuantity);
            productRepository.save(product);

            return ResponseEntity.status(HttpStatus.OK).body(
                    new ResponseResult("ok", "Cart item quantity updated successfully", updatedCartItem, 1)
            );
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    new ResponseResult("error", "Quantity in cart cannot be greater than the quantity in the product", null, 0)
            );
        }
    }

    public ResponseEntity<ResponseResult> updateCartItem(@RequestBody CartItemRequest newCartItem, @PathVariable Long id) {

        Product product = productRepository.findById(newCartItem.getProductId()).orElseThrow();
        User user = userRepository.findById(newCartItem.getUserId()).orElseThrow();
        Optional<CartItem> updatedCartItem = cartItemRepository.findById(id)
                .map(cartItem -> {
                    cartItem.setUser(user);
                    cartItem.setProduct(product);
                    cartItem.setQuantity(newCartItem.getQuantity());
                    return cartItemRepository.save(cartItem);
                });
        return ResponseEntity.status(HttpStatus.OK).body(
                new ResponseResult("ok", "Update Customer successfully", updatedCartItem,1)
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

//    public ResponseEntity<ResponseResult> findItemByUserId(@PathVariable Long id) {
//        List<CartItem> foundCartItem = cartItemRepository.findByUserId(id);
//        return !foundCartItem.isEmpty() ?
//                ResponseEntity.status(HttpStatus.OK).body(
//                        new ResponseResult("ok", "Query product item successfully", foundCartItem, foundCartItem.size())
//                        //you can replace "ok" with your defined "error code"
//                ):
//                ResponseEntity.status(HttpStatus.OK).body(
//                        new ResponseResult("failed", "Cannot find product item with id = "+id, "",foundCartItem.size())
//                );
//    }

//    public List<CartItem> findCartItemsByProductName(String name) {
//        List<CartItem> cartItems = cartItemRepository.findAll();
//        List<CartItem> result = new ArrayList<>();
//        for (CartItem cartItem : cartItems) {
//            if (cartItem.getProduct().getName().toLowerCase().contains(name.toLowerCase())) {
//                result.add(cartItem);
//            }
//        }
//        return result;
//    }

//    public ResponseEntity<CartItem> findItemByUserId(Long id) {
//        List<CartItem> cartItems = cartItemRepository.findByUserId(id);
//
//        Double totalPrice = cartItems.stream()
//                .mapToDouble(item -> item.getProduct().getOutputPrice() * item.getQuantity())
//                .sum();
//
//        Integer totalQuantity = cartItems.stream()
//                .mapToInt(CartItem::getQuantity)
//                .sum();
//
//        Map<Product, Integer> productQuantities = cartItems.stream()
//                .collect(Collectors.groupingBy(CartItem::getProduct,
//                        Collectors.summingInt(CartItem::getQuantity)));
//
//        List<ProductResponse> productDtos = cartItems.stream()
//                .map(cartItem -> {
//                    ProductResponse productDto = new ProductResponse();
//                    productDto.setCart_item_id(cartItem.getId());
//                    productDto.setId(cartItem.getProduct().getId());
//                    productDto.setQuantity(cartItem.getQuantity());
//                    productDto.setName(cartItem.getProduct().getName());
//                    productDto.setDescription(cartItem.getProduct().getDescription());
//                    productDto.setInputPrice(cartItem.getProduct().getInputPrice());
//                    productDto.setOutputPrice(cartItem.getProduct().getOutputPrice());
//                    productDto.setBrand(cartItem.getProduct().getBrand());
//                    productDto.setColor(cartItem.getProduct().getColor());
//                    productDto.setProductImage(cartItem.getProduct().getProductImage());
//                    productDto.setInventory(cartItem.getProduct().getInventory());
//                    productDto.setSold(cartItem.getProduct().getSold());
//                    productDto.setTotal(cartItem.getQuantity() * cartItem.getProduct().getOutputPrice());
//                    return productDto;
//                })
//                .collect(Collectors.toList());
//        Double taxRate = 0.05;
//        Double tax = totalPrice * taxRate;
//
//        CartResponse cartResponse = new CartResponse();
//        cartResponse.setId(1L); // set cart id to 1
//        cartResponse.setQuantity(totalQuantity);
//        cartResponse.setCount(productQuantities.size());
//        cartResponse.setTotal(totalPrice);
//        cartResponse.setTax(tax);
//        cartResponse.setInvoice(totalPrice + tax);
//        cartResponse.setProductResponses(productDtos);
//
//        return ResponseEntity.ok().body(cartResponse);
//    }
}
