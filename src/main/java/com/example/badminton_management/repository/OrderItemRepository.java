package com.example.badminton_management.repository;

import com.example.badminton_management.model.Order;
import com.example.badminton_management.model.OrderItem;

import com.example.badminton_management.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface OrderItemRepository extends JpaRepository<OrderItem,Long> {
    List<OrderItem> findByOrder(Order order);
    Optional<OrderItem> findByOrderAndProduct(Order order, Product product);
    Optional<OrderItem> findByIdAndOrder(Long id, Order order);
}
