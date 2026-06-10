package com.example.badminton_management.model;

import com.example.badminton_management.enums.PaymentMethod;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "order_payments")
public class OrderPayment extends Payment{
    @ManyToOne
    @JoinColumn(name = "order_id",nullable = false)
    private Order order;

    public OrderPayment() {

    }

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }


}
