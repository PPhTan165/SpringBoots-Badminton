package com.example.badminton_management.dto.order;

import jakarta.validation.constraints.NotBlank;

public class CreateOrderRequest {
    @NotBlank
    private String shippingName;

    @NotBlank
    private String shippingPhone;

    @NotBlank
    private String shippingAddress;

    private String note;

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
}
