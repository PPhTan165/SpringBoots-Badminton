package com.example.badminton_management.repository;

import com.example.badminton_management.dto.cart.CartItemResponse;
import com.example.badminton_management.model.Cart;
import com.example.badminton_management.model.CartItem;
import com.example.badminton_management.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CartItemRepository extends JpaRepository<CartItem,Long> {
    List<CartItem> findByCart(Cart cart);
    Optional<CartItem> findByCartAndProduct(Cart cart, Product product);
}
