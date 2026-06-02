package com.example.badminton_management.dto.product;

import com.example.badminton_management.enums.ProductStatus;
import com.example.badminton_management.model.Product;
import jakarta.validation.constraints.NotNull;

public class UpdateProductStatusRequest {
    @NotNull(message = "Status is required")
    private ProductStatus status;

    public ProductStatus getStatus() {
        return status;
    }

    public void setStatus(ProductStatus status) {
        this.status = status;
    }
}
