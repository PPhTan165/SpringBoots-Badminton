package com.example.badminton_management.repository;

import com.example.badminton_management.model.OrderPayment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderPaymentRepository extends JpaRepository<OrderPayment,Long> {
}
