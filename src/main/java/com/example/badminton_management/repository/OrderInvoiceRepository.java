package com.example.badminton_management.repository;

import com.example.badminton_management.model.OrderInvoice;
import com.example.badminton_management.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface OrderInvoiceRepository extends JpaRepository<OrderInvoice,Long> {
    List<OrderInvoice> findByOrderPaymentOrderUser(User user);
    Optional<OrderInvoice> findByOrderPaymentOrderUserAndId(User user, Long invoiceId);
    boolean existsByOrderPaymentId(Long orderPaymentId);
}
