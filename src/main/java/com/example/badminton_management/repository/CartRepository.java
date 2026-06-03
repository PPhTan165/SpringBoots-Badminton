package com.example.badminton_management.repository;

import com.example.badminton_management.enums.CartStatus;
import com.example.badminton_management.model.Cart;
import com.example.badminton_management.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CartRepository extends JpaRepository<Cart,Long> {
    Optional<Cart> findByUserAndStatus(User user, CartStatus status);
}
