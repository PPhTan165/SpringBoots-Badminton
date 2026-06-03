package com.example.badminton_management.repository;

import com.example.badminton_management.model.Order;
import com.example.badminton_management.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import javax.swing.text.html.Option;
import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order,Long> {
    List<Order> findByUser(User user);
    boolean existsCodeNotId(String code, Long id);
}
