package com.example.badminton_management.dto.cart;

import java.math.BigDecimal;

public class CartItemResponse {

    private Long cartItemId;

    private Long productId;

    private String productName;

    private String imageUrl;

    private BigDecimal unitPrice;

    private Integer quantity;

    private BigDecimal subtotal;

    public CartItemResponse() {
    }

    public Long getCartItemId() {
        return cartItemId;
    }

    public Long getProductId() {
        return productId;
    }

    public String getProductName() {
        return productName;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public BigDecimal getSubtotal() {
        return subtotal;
    }

    public void setCartItemId(Long cartItemId) {
        this.cartItemId = cartItemId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public void setUnitPrice(BigDecimal unitPrice) {
        this.unitPrice = unitPrice;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public void setSubtotal(BigDecimal subtotal) {
        this.subtotal = subtotal;
    }
}
