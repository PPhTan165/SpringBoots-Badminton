package com.example.badminton_management.controller;

import com.example.badminton_management.dto.payment.CreatePaymentRequest;
import com.example.badminton_management.dto.payment.OrderPaymentResponse;
import com.example.badminton_management.service.OrderPaymentService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class OrderPaymentController {
    private final OrderPaymentService orderPaymentService;

    public OrderPaymentController(OrderPaymentService orderPaymentService) {
        this.orderPaymentService = orderPaymentService;
    }

    @GetMapping("/orders/{orderId}/payment")
    public ResponseEntity<OrderPaymentResponse> getMyOrderPayment(@PathVariable Long orderId) {
        return ResponseEntity.ok(orderPaymentService.getMyOrderPayment(orderId));
    }

    @GetMapping("/order-payments")
    public ResponseEntity<List<OrderPaymentResponse>> getMyOrderPayments() {
        return ResponseEntity.ok(orderPaymentService.getMyOrderPayments());
    }

    @GetMapping("/order-payments/{orderPaymentId}")
    public ResponseEntity<OrderPaymentResponse> getMyOrderPaymentById(@PathVariable Long orderPaymentId) {
        return ResponseEntity.ok(orderPaymentService.getMyOrderPaymentById(orderPaymentId));
    }

    @PostMapping("/orders/{orderId}/payment")
    public ResponseEntity<OrderPaymentResponse> createOrderPayment(
            @PathVariable Long orderId,
            @Valid @RequestBody CreatePaymentRequest request
    ) {
        OrderPaymentResponse response = orderPaymentService.createOrderPayment(orderId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
