package com.example.badminton_management.controller;

import com.example.badminton_management.dto.invoice.OrderInvoiceResponse;

import com.example.badminton_management.service.OrderInvoiceService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/order-invoices")
@PreAuthorize("hasRole('ADMIN')")
public class AdminOrderInvoice {
    private final OrderInvoiceService orderInvoiceService;

    public AdminOrderInvoice(OrderInvoiceService orderInvoiceService) {
        this.orderInvoiceService = orderInvoiceService;
    }

    @GetMapping
    public ResponseEntity<List<OrderInvoiceResponse>> getAllInvoice(){
        return ResponseEntity.ok().body(orderInvoiceService.getAllInvoice());
    }

    @GetMapping("/{orderInvoiceId}")
    public ResponseEntity<OrderInvoiceResponse> getInvoiceById(@PathVariable Long orderInvoiceId){
        return ResponseEntity.ok().body(orderInvoiceService.getInvoiceById(orderInvoiceId));
    }

    @PostMapping("/{orderPaymentId}/invoice")
    public ResponseEntity<OrderInvoiceResponse> createOrderInvoice(@PathVariable Long orderPaymentId) {
        return ResponseEntity.status(HttpStatus.CREATED).body(orderInvoiceService.createOrderInvoice(orderPaymentId));
    }
}
