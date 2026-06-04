package com.example.badminton_management.service;

import com.example.badminton_management.dto.payment.CreatePaymentRequest;
import com.example.badminton_management.dto.payment.OrderPaymentResponse;
import com.example.badminton_management.dto.payment.UpdatePaymentStatusRequest;
import com.example.badminton_management.enums.PaymentStatus;
import com.example.badminton_management.exception.BadRequestException;
import com.example.badminton_management.exception.ResourceNotFoundException;
import com.example.badminton_management.jwt.CurrentUserHelper;
import com.example.badminton_management.model.Order;
import com.example.badminton_management.model.OrderPayment;
import com.example.badminton_management.model.User;
import com.example.badminton_management.repository.OrderPaymentRepository;
import com.example.badminton_management.repository.OrderRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class OrderPaymentService {
    private final OrderPaymentRepository orderPaymentRepository;
    private final OrderRepository orderRepository;
    private final CurrentUserHelper currentUserHelper;

    public OrderPaymentService(
            OrderPaymentRepository orderPaymentRepository,
            CurrentUserHelper currentUserHelper,
            OrderRepository orderRepository) {
        this.orderPaymentRepository = orderPaymentRepository;
        this.currentUserHelper = currentUserHelper;
        this.orderRepository = orderRepository;
    }

    private OrderPaymentResponse mapToResponse(OrderPayment orderPayment) {
        OrderPaymentResponse response = new OrderPaymentResponse();

        response.setOrderId(orderPayment.getOrder().getId());
        response.setOrderCode(orderPayment.getOrder().getOrderCode());

        response.setUserId(orderPayment.getOrder().getUser().getId());
        response.setUsername(orderPayment.getOrder().getUser().getUsername());

        response.setAmount(orderPayment.getAmount());
        response.setPaymentMethod(orderPayment.getPaymentMethod().name());
        response.setPaymentStatus(orderPayment.getPaymentStatus().name());
        response.setTransactionCode(orderPayment.getTransactionCode());

        return response;
    }

    public OrderPaymentResponse getMyOrderPayment(Long orderId) {
        if (orderId == null || orderId <= 0) {
            throw new IllegalArgumentException("Id must be greater than 0");
        }

        User user = getCurrentUser();

        OrderPayment orderPayment = orderPaymentRepository.findByOrderIdAndOrderUser(orderId, user)
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found for order id: " + orderId
                ));
        return mapToResponse(orderPayment);
    }

    public OrderPaymentResponse createOrderPayment(Long orderId,CreatePaymentRequest request){
        if (orderId == null || orderId <= 0) {
            throw new IllegalArgumentException("Id must be greater than 0");
        }

        User user = getCurrentUser();

        Order order = orderRepository.findByIdAndUser(orderId,user)
                .orElseThrow(()->new ResourceNotFoundException("Order not found with id: " +orderId));

        if(orderPaymentRepository.findByOrder(order).isPresent()){
            throw new BadRequestException("Payment already exists for order id: "+orderId);
        }

        OrderPayment orderPayment = new OrderPayment();
        orderPayment.setOrder(order);
        orderPayment.setAmount(order.getTotalAmount());
        orderPayment.setPaymentMethod(request.getPaymentMethod());
        orderPayment.setPaymentStatus(PaymentStatus.PENDING);
        orderPayment.setTransactionCode("PAY-" + System.currentTimeMillis());

        OrderPayment savedPayment = orderPaymentRepository.save(orderPayment);

        return mapToResponse(savedPayment);
    }

    public List<OrderPaymentResponse> getAllOrderPayment(){
        return orderPaymentRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Transactional
    public OrderPaymentResponse updateOrderPaymentStatus(Long orderPaymentId, UpdatePaymentStatusRequest request){
        if(orderPaymentId <= 0){
            throw new IllegalArgumentException("Id must be greater than 0");
        }

        OrderPayment payment = orderPaymentRepository.findById(orderPaymentId)
                .orElseThrow(()->new ResourceNotFoundException("Order payment not found with id: "+orderPaymentId));

        PaymentStatus currentStatus = payment.getPaymentStatus();
        PaymentStatus newStatus = request.getPaymentStatus();
        
        if(currentStatus == newStatus){
            return mapToResponse(payment);
        }

        if(!isValidPaymentStatusTransition(currentStatus,newStatus)){
            throw new BadRequestException("Cannot update payment status from: "+currentStatus + " to " + newStatus);
        }

        if(newStatus == PaymentStatus.PAID){
            payment.setPaidAt(LocalDateTime.now());
        }

        OrderPayment updatePayment = orderPaymentRepository.save(payment);
        return mapToResponse(updatePayment);
    }

    private boolean isValidPaymentStatusTransition(PaymentStatus currentStatus, PaymentStatus newStatus) {
        return switch (currentStatus) {
            case PENDING -> newStatus == PaymentStatus.PAID || newStatus == PaymentStatus.FAILED;
            case PAID -> newStatus == PaymentStatus.REFUNDED;
            case FAILED -> false;
            case REFUNDED -> false;
        };
    }

    private User getCurrentUser() {
        return currentUserHelper.getCurrentUser();
    }
}
