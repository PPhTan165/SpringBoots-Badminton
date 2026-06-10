package com.example.badminton_management.controller;

import com.example.badminton_management.dto.invoice.OrderInvoiceResponse;
import com.example.badminton_management.service.OrderInvoiceService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/order-invoices")
public class OrderInvoiceController {
    private final OrderInvoiceService service;

    public OrderInvoiceController(OrderInvoiceService service) {
        this.service = service;
    }

    @GetMapping()
    public ResponseEntity<List<OrderInvoiceResponse>> getMyInvoice() {
        return ResponseEntity.ok().body(service.getMyInvoice());
    }

    @GetMapping("/{orderInvoiceId}")
    public ResponseEntity<OrderInvoiceResponse> getMyInvoiceById(@PathVariable Long orderInvoiceId) {
        OrderInvoiceResponse response = service.getMyInvoiceById(orderInvoiceId);
        return ResponseEntity.ok().body(response);
    }


}
