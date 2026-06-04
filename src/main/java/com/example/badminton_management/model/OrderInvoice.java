package com.example.badminton_management.model;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "order_invoices")
public class OrderInvoice {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_payment_id",nullable = false)
    private OrderPayment orderPayment;

    @Column(name = "invoice_code",nullable = false)
    private String invoiceCode;

    @Column(name = "total_amount",nullable = false)
    private BigDecimal totalAmount;

    private String note;

    @Column(name = "issued_at",nullable = false)
    private LocalDateTime issuedAt = LocalDateTime.now();
}
