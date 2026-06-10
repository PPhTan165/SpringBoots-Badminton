package com.example.badminton_management.service;

import com.example.badminton_management.dto.payment.CreatePaymentRequest;
import com.example.badminton_management.dto.payment.OrderPaymentResponse;
import com.example.badminton_management.dto.payment.UpdatePaymentStatusRequest;
import com.example.badminton_management.enums.OrderStatus;
import com.example.badminton_management.enums.PaymentMethod;
import com.example.badminton_management.enums.PaymentStatus;
import com.example.badminton_management.exception.BadRequestException;
import com.example.badminton_management.jwt.CurrentUserHelper;
import com.example.badminton_management.model.Order;
import com.example.badminton_management.model.OrderPayment;
import com.example.badminton_management.model.User;
import com.example.badminton_management.repository.OrderPaymentRepository;
import com.example.badminton_management.repository.OrderRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderPaymentServiceTest {

    @Mock
    private OrderPaymentRepository orderPaymentRepository;

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private CurrentUserHelper currentUserHelper;

    @InjectMocks
    private OrderPaymentService orderPaymentService;

    @Test
    void createOrderPaymentCreatesPendingPaymentForCurrentUserOrder() {
        User user = createUser(1L, "alice");
        Order order = createOrder(10L, user, OrderStatus.PENDING, new BigDecimal("250000"));
        CreatePaymentRequest request = new CreatePaymentRequest();
        request.setPaymentMethod(PaymentMethod.MOMO);

        when(currentUserHelper.getCurrentUser()).thenReturn(user);
        when(orderRepository.findByIdAndUser(10L, user)).thenReturn(Optional.of(order));
        when(orderPaymentRepository.findByOrder(order)).thenReturn(Optional.empty());
        when(orderPaymentRepository.save(any(OrderPayment.class))).thenAnswer(invocation -> {
            OrderPayment payment = invocation.getArgument(0);
            payment.setId(99L);
            payment.setCreatedAt(LocalDateTime.now());
            return payment;
        });

        OrderPaymentResponse response = orderPaymentService.createOrderPayment(10L, request);

        assertEquals(99L, response.getId());
        assertEquals(10L, response.getOrderId());
        assertEquals("alice", response.getUsername());
        assertEquals("MOMO", response.getPaymentMethod());
        assertEquals("PENDING", response.getPaymentStatus());
        assertEquals(new BigDecimal("250000"), response.getAmount());
        assertNotNull(response.getTransactionCode());
    }

    @Test
    void createOrderPaymentRejectsCompletedOrder() {
        User user = createUser(1L, "alice");
        Order order = createOrder(10L, user, OrderStatus.COMPLETED, new BigDecimal("250000"));
        CreatePaymentRequest request = new CreatePaymentRequest();
        request.setPaymentMethod(PaymentMethod.CASH);

        when(currentUserHelper.getCurrentUser()).thenReturn(user);
        when(orderRepository.findByIdAndUser(10L, user)).thenReturn(Optional.of(order));

        BadRequestException exception = assertThrows(
                BadRequestException.class,
                () -> orderPaymentService.createOrderPayment(10L, request)
        );

        assertEquals("Order is not eligible for payment", exception.getMessage());
        verify(orderPaymentRepository, never()).save(any());
    }

    @Test
    void getMyOrderPaymentsReturnsOnlyCurrentUsersPayments() {
        User user = createUser(1L, "alice");
        OrderPayment payment1 = createPayment(101L, createOrder(10L, user, OrderStatus.PENDING, new BigDecimal("100000")));
        OrderPayment payment2 = createPayment(102L, createOrder(11L, user, OrderStatus.CONFIRMED, new BigDecimal("200000")));

        when(currentUserHelper.getCurrentUser()).thenReturn(user);
        when(orderPaymentRepository.findByOrderUser(user)).thenReturn(List.of(payment1, payment2));

        List<OrderPaymentResponse> responses = orderPaymentService.getMyOrderPayments();

        assertEquals(2, responses.size());
        assertEquals(101L, responses.get(0).getId());
        assertEquals(102L, responses.get(1).getId());
    }

    @Test
    void getMyOrderPaymentByIdReturnsOwnedPayment() {
        User user = createUser(1L, "alice");
        OrderPayment payment = createPayment(101L, createOrder(10L, user, OrderStatus.PENDING, new BigDecimal("100000")));

        when(currentUserHelper.getCurrentUser()).thenReturn(user);
        when(orderPaymentRepository.findByIdAndOrderUser(101L, user)).thenReturn(Optional.of(payment));

        OrderPaymentResponse response = orderPaymentService.getMyOrderPaymentById(101L);

        assertEquals(101L, response.getId());
        assertEquals(10L, response.getOrderId());
        assertEquals("alice", response.getUsername());
    }

    @Test
    void updateOrderPaymentStatusSetsStatusAndPaidAtWhenPaid() {
        User user = createUser(1L, "alice");
        OrderPayment payment = createPayment(101L, createOrder(10L, user, OrderStatus.PENDING, new BigDecimal("100000")));
        payment.setPaymentStatus(PaymentStatus.PENDING);
        payment.setPaidAt(null);

        UpdatePaymentStatusRequest request = new UpdatePaymentStatusRequest();
        request.setPaymentStatus(PaymentStatus.PAID);

        when(orderPaymentRepository.findById(101L)).thenReturn(Optional.of(payment));
        when(orderPaymentRepository.save(payment)).thenReturn(payment);

        OrderPaymentResponse response = orderPaymentService.updateOrderPaymentStatus(101L, request);

        assertEquals("PAID", response.getPaymentStatus());
        assertNotNull(response.getPaidAt());
    }

    private User createUser(Long id, String username) {
        User user = new User();
        ReflectionTestUtils.setField(user, "id", id);
        user.setUsername(username);
        return user;
    }

    private Order createOrder(Long id, User user, OrderStatus status, BigDecimal totalAmount) {
        Order order = new Order();
        order.setId(id);
        order.setUser(user);
        order.setOrderCode("ORD-" + id);
        order.setOrderStatus(status);
        order.setTotalAmount(totalAmount);
        return order;
    }

    private OrderPayment createPayment(Long id, Order order) {
        OrderPayment payment = new OrderPayment();
        payment.setId(id);
        payment.setOrder(order);
        payment.setAmount(order.getTotalAmount());
        payment.setPaymentMethod(PaymentMethod.MOMO);
        payment.setPaymentStatus(PaymentStatus.PENDING);
        payment.setTransactionCode("PAY-" + id);
        payment.setCreatedAt(LocalDateTime.now());
        return payment;
    }
}
