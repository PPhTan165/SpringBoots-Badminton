package com.example.badminton_management.repository;

import com.example.badminton_management.enums.PaymentStatus;
import com.example.badminton_management.model.Order;
import com.example.badminton_management.model.OrderPayment;
import com.example.badminton_management.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OrderPaymentRepository extends JpaRepository<OrderPayment,Long> {
    Optional<OrderPayment> findByOrderIdAndOrderUser(Long orderId, User user);
    Optional<OrderPayment> findByOrder(Order order);
    Optional<OrderPayment> findByOrderAndStatus(Order order, PaymentStatus paymentStatus);
}
