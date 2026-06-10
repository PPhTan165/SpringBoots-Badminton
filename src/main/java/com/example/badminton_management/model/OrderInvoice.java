package com.example.badminton_management.model;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "order_invoices")
public class OrderInvoice extends Invoice {

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_payment_id",nullable = false, unique = true)
    private OrderPayment orderPayment;

    public OrderInvoice() {
    }

    public OrderPayment getOrderPayment() {
        return orderPayment;
    }

    public void setOrderPayment(OrderPayment orderPayment) {
        this.orderPayment = orderPayment;
    }
}
