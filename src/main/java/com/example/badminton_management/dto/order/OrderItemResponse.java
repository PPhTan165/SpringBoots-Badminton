package com.example.badminton_management.dto.order;

import java.math.BigDecimal;

public class OrderItemResponse {
    private Long id;
    private Long orderId;
    private String orderCode;
    private Long productId;
    private String productName;
    private Integer quantity;
    private BigDecimal unitPrice;
    private BigDecimal subTotal;

    public Long getId() {
        return id;
    }

    public Long getOrderId() {
        return orderId;
    }

    public Long getProductId() {
        return productId;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

    public BigDecimal getSubTotal() {
        return subTotal;
    }

    public String getProductName() {
        return productName;
    }

    public String getOrderCode() {
        return orderCode;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public void setUnitPrice(BigDecimal unitPrice) {
        this.unitPrice = unitPrice;
    }

    public void setSubTotal(BigDecimal subTotal) {
        this.subTotal = subTotal;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public void setOrderCode(String orderCode) {
        this.orderCode = orderCode;
    }
}
