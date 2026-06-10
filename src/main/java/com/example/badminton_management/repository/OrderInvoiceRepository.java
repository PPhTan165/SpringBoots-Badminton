package com.example.badminton_management.repository;

import com.example.badminton_management.model.OrderInvoice;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderInvoiceRepository extends JpaRepository<OrderInvoice,Long> {
    
}
