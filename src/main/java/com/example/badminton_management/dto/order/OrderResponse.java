package com.example.badminton_management.dto.order;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.math.BigDecimal;

@JsonPropertyOrder({
        "id",
        "userId",
        "userName",
        "orderCode",
        "totalAmount",
        "orderStatus",
        "shippingName",
        "shippingPhone",
        "shippingAddress",
        "note",
        "voucherId",
        "voucherName",
})

public class OrderResponse {
    private Long id;

    private String orderCode;
    private BigDecimal totalAmound;
    private String orderStatus;
    private String shippingName;
    private String shippingPhone;
    private String shippingAddress;
    private String note;

    private Long userId;
    private String userName;

    private Long voucherId;
    private String VoucherName;

    public OrderResponse() {
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setOrderCode(String orderCode) {
        this.orderCode = orderCode;
    }

    public void setTotalAmound(BigDecimal totalAmound) {
        this.totalAmound = totalAmound;
    }

    public void setOrderStatus(String orderStatus) {
        this.orderStatus = orderStatus;
    }

    public void setShippingName(String shippingName) {
        this.shippingName = shippingName;
    }

    public void setShippingPhone(String shippingPhone) {
        this.shippingPhone = shippingPhone;
    }

    public void setShippingAddress(String shippingAddress) {
        this.shippingAddress = shippingAddress;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setVoucherId(Long voucherId) {
        this.voucherId = voucherId;
    }

    public void setVoucherName(String voucherName) {
        VoucherName = voucherName;
    }

    public Long getId() {
        return id;
    }

    public String getOrderCode() {
        return orderCode;
    }

    public BigDecimal getTotalAmound() {
        return totalAmound;
    }

    public String getOrderStatus() {
        return orderStatus;
    }

    public String getShippingName() {
        return shippingName;
    }

    public String getShippingPhone() {
        return shippingPhone;
    }

    public String getShippingAddress() {
        return shippingAddress;
    }

    public String getNote() {
        return note;
    }

    public Long getUserId() {
        return userId;
    }

    public String getUserName() {
        return userName;
    }

    public Long getVoucherId() {
        return voucherId;
    }

    public String getVoucherName() {
        return VoucherName;
    }
}
