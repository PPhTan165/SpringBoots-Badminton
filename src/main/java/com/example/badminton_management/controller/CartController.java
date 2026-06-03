package com.example.badminton_management.controller;

import com.example.badminton_management.dto.cart.AddToCartRequest;
import com.example.badminton_management.dto.cart.CartResponse;
import com.example.badminton_management.dto.cart.UpdateCartRequest;
import com.example.badminton_management.service.CartService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/carts")
public class CartController {
    private final CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    @GetMapping
    public ResponseEntity<CartResponse> getMyCart(){
        return ResponseEntity.ok(cartService.getMyCart());
    }

    @PostMapping("/items")
    public ResponseEntity<CartResponse> addToCart(@Valid @RequestBody AddToCartRequest request){
        return ResponseEntity.status(HttpStatus.CREATED).body(cartService.addToCart(request));
    }

    @PatchMapping("/items/{cartItemId}")
    public ResponseEntity<CartResponse> updateCartItem(@PathVariable Long cartItemId, @Valid @RequestBody UpdateCartRequest request){
        return ResponseEntity.ok(cartService.updateCartItem(cartItemId,request));
    }

    @DeleteMapping("/items/{cartItemId}")
    public ResponseEntity<CartResponse> removeCartItem(@PathVariable Long cartItemId){
        return ResponseEntity.ok(cartService.removeCartItem(cartItemId));
    }
}
