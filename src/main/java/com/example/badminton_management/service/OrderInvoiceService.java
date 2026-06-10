package com.example.badminton_management.service;

import com.example.badminton_management.repository.OrderInvoiceRepository;
import org.springframework.stereotype.Service;

@Service
public class OrderInvoiceService {

    private final OrderInvoiceRepository orderInvoiceRepository;

    public OrderInvoiceService(OrderInvoiceRepository orderInvoiceRepository) {
        this.orderInvoiceRepository = orderInvoiceRepository;
    }


}
