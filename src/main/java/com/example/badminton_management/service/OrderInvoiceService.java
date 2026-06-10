package com.example.badminton_management.service;

import com.example.badminton_management.dto.invoice.OrderInvoiceResponse;
import com.example.badminton_management.enums.PaymentStatus;
import com.example.badminton_management.exception.BadRequestException;
import com.example.badminton_management.exception.ResourceNotFoundException;
import com.example.badminton_management.jwt.CurrentUserHelper;
import com.example.badminton_management.model.*;
import com.example.badminton_management.repository.OrderInvoiceRepository;
import com.example.badminton_management.repository.OrderPaymentRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class OrderInvoiceService {

    private final OrderInvoiceRepository orderInvoiceRepository;
    private final CurrentUserHelper currentUserHelper;
    private final OrderPaymentRepository orderPaymentRepository;

    public OrderInvoiceService(
            OrderInvoiceRepository orderInvoiceRepository,
            CurrentUserHelper currentUserHelper,
            OrderPaymentRepository orderPaymentRepository) {
        this.orderInvoiceRepository = orderInvoiceRepository;
        this.currentUserHelper = currentUserHelper;
        this.orderPaymentRepository = orderPaymentRepository;
    }

    private OrderInvoiceResponse mapToResponse(OrderInvoice invoice){
        OrderPayment payment = invoice.getOrderPayment();
        Order order = payment.getOrder();

        OrderInvoiceResponse response = new OrderInvoiceResponse();
        response.setId(invoice.getId());
        response.setOrderPaymentId(payment.getId());
        response.setUsername(order.getUser().getUsername());
        response.setShippingName(order.getShippingName());
        response.setShippingPhone(order.getShippingPhone());
        response.setShippingAddress(order.getShippingAddress());
        response.setInvoiceCode(invoice.getInvoiceCode());
        response.setTotalAmount(invoice.getTotalAmount());
        response.setPaymentStatus(payment.getPaymentStatus());
        response.setNote(invoice.getNote());
        response.setIssuedAt(invoice.getIssuedAt());

        return response;
    }

    public List<OrderInvoiceResponse> getMyInvoice(){
        User user = getCurrentUser();

        return orderInvoiceRepository.findInvoiceUser(user).stream().map(this::mapToResponse).toList();
    }

    public OrderInvoiceResponse getMyInvoiceById(Long invoiceId){
        if(invoiceId <= 0){
            throw new IllegalArgumentException("Id must be greater than 0");
        }

        User user = getCurrentUser();

        OrderInvoice invoice = orderInvoiceRepository.findInvoiceUserAndId(user,invoiceId)
                .orElseThrow(()->new ResourceNotFoundException("Invoice not found with id: "+ invoiceId));

        return mapToResponse(invoice);
    }

    public OrderInvoiceResponse createOrderInvoice(Long orderPaymentId){
        if(orderPaymentId==null || orderPaymentId <= 0){
            throw new IllegalArgumentException("Id must be greater than 0");
        }

        OrderPayment orderPayment = orderPaymentRepository.findById(orderPaymentId)
                .orElseThrow(()->new ResourceNotFoundException(
                        "Order payment not found with id" + orderPaymentId));

        if(orderPayment.getPaymentStatus() != PaymentStatus.PAID){
            throw new BadRequestException("Invoice can only be created for PAID payments");
        }

        if(orderInvoiceRepository.existsByOrderPaymentId(orderPaymentId)){
            throw new BadRequestException("Invoice already exists for order payment id: " + orderPaymentId);
        }

        Order order = orderPayment.getOrder();

        OrderInvoice invoice = new OrderInvoice();
        invoice.setOrderPayment(orderPayment);
        invoice.setInvoiceCode("INV-" + System.currentTimeMillis());
        invoice.setTotalAmount(orderPayment.getAmount());
        invoice.setNote(order.getNote());
        invoice.setIssuedAt(
                orderPayment.getPaidAt() != null ? orderPayment.getPaidAt() : LocalDateTime.now()
        );

        OrderInvoice savedInvoice = orderInvoiceRepository.save(invoice);
        return mapToResponse(savedInvoice);
    }

    private User getCurrentUser(){
        return currentUserHelper.getCurrentUser();
    }
}
