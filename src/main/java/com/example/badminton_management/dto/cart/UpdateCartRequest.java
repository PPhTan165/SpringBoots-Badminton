package com.example.badminton_management.dto.cart;

import jakarta.validation.constraints.Min;

public class UpdateCartRequest {
    @Min(1)
    private Integer quantity;
}
