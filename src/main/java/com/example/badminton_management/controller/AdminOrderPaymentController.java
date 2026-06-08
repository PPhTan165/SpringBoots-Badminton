package com.example.badminton_management.controller;

import com.example.badminton_management.dto.payment.OrderPaymentResponse;
import com.example.badminton_management.dto.payment.UpdatePaymentStatusRequest;
import com.example.badminton_management.service.OrderPaymentService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/order-payments")
@PreAuthorize("hasRole('ADMIN')")
public class AdminOrderPaymentController {
    private final OrderPaymentService orderPaymentService;

    public AdminOrderPaymentController(OrderPaymentService orderPaymentService) {
        this.orderPaymentService = orderPaymentService;
    }

    @GetMapping
    public ResponseEntity<List<OrderPaymentResponse>> getAllOrderPayments() {
        return ResponseEntity.ok(orderPaymentService.getAllOrderPayment());
    }

    @PatchMapping("/{orderPaymentId}/status")
    public ResponseEntity<OrderPaymentResponse> updateOrderPaymentStatus(
            @PathVariable Long orderPaymentId,
            @Valid @RequestBody UpdatePaymentStatusRequest request
    ) {
        return ResponseEntity.ok(orderPaymentService.updateOrderPaymentStatus(orderPaymentId, request));
    }
}
