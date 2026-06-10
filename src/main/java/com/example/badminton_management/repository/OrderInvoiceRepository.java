package com.example.badminton_management.repository;

import com.example.badminton_management.model.OrderInvoice;
import com.example.badminton_management.model.OrderPayment;
import com.example.badminton_management.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface OrderInvoiceRepository extends JpaRepository<OrderInvoice,Long> {
    List<OrderInvoice> findInvoiceUser(User user);
    Optional<OrderInvoice> findInvoiceUserAndId(User user, Long invoiceId);
    boolean existsByOrderPaymentId(Long orderPaymentId);
}
