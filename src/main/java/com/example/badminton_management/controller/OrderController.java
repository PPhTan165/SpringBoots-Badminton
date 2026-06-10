package com.example.badminton_management.controller;

import com.example.badminton_management.dto.order.CreateOrderRequest;
import com.example.badminton_management.dto.order.OrderResponse;
import com.example.badminton_management.dto.order.UpdateOrderStatusRequest;
import com.example.badminton_management.service.OrderService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping
    public ResponseEntity<List<OrderResponse>> getMyOrder(){
        return ResponseEntity.ok(orderService.getMyOrders());
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<OrderResponse> getMyOrderById(@PathVariable Long orderId){
        return ResponseEntity.ok(orderService.getMyOrderById(orderId));
    }

    @PostMapping
    public ResponseEntity<OrderResponse> createOrder(@Valid @RequestBody CreateOrderRequest request){
        OrderResponse response = orderService.createOrder(request);
        return  ResponseEntity.status(HttpStatus.CREATED).body(response);
    }


}
