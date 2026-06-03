package com.example.badminton_management.service;

import com.example.badminton_management.dto.cart.AddToCartRequest;
import com.example.badminton_management.dto.cart.CartItemResponse;
import com.example.badminton_management.dto.cart.CartResponse;
import com.example.badminton_management.dto.cart.UpdateCartRequest;
import com.example.badminton_management.dto.product.ProductResponse;

import com.example.badminton_management.enums.CartStatus;
import com.example.badminton_management.exception.BadRequestException;
import com.example.badminton_management.exception.ResourceNotFoundException;
import com.example.badminton_management.model.*;
import com.example.badminton_management.repository.CartItemRepository;
import com.example.badminton_management.repository.CartRepository;
import com.example.badminton_management.repository.ProductRepository;
import com.example.badminton_management.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class CartService {
    private CartRepository cartRepository;
    private CartItemRepository cartItemRepository;
    private UserRepository userRepository;
    private ProductRepository productRepository;

    public CartService(CartRepository cartRepository, UserRepository userRepository, CartItemRepository cartItemRepository, ProductRepository productRepository) {
        this.cartRepository = cartRepository;
        this.userRepository = userRepository;
        this.cartItemRepository = cartItemRepository;
        this.productRepository = productRepository;
    }

    //Chuyển từng CartItem sang CartItemResponse để trả về API
    private CartItemResponse mapToCartItemResponse(CartItem cartItem) {
        CartItemResponse response = new CartItemResponse();

        response.setCartItemId(cartItem.getId());
        response.setQuantity(cartItem.getQuantity());
        response.setUnitPrice(cartItem.getUnitPrice());
        response.setSubtotal(cartItem.getSubtotal());

        if (cartItem.getProduct() != null) {
            response.setProductId(cartItem.getProduct().getId());
            response.setProductName(cartItem.getProduct().getName());
        }

        return response;
    }

    private CartResponse mapToCartResponse(Cart cart) {
        List<CartItemResponse> items = cartItemRepository.findByCart(cart)
                .stream()
                .map(this::mapToCartItemResponse)
                .toList();

        CartResponse response = new CartResponse();

        response.setCartId(cart.getId());
        response.setItems(items);
        response.setTotalItems(items.stream().mapToInt(CartItemResponse::getQuantity).sum());
        response.setTotalAmount(items.stream().map(CartItemResponse::getSubtotal).reduce(BigDecimal.ZERO, BigDecimal::add));
        return response;
    }

    public CartResponse getMyCart() {
        User user = getCurrentUser();

        Cart cart = cartRepository.findByUserAndStatus(user, CartStatus.ACTIVE).orElseGet(() -> {
            Cart newCart = new Cart();
            newCart.setUser(user);
            newCart.setStatus(CartStatus.ACTIVE);
            return cartRepository.save(newCart);
        });

        return mapToCartResponse(cart);
    }

    @Transactional
    public CartResponse addToCart(AddToCartRequest request) {
        if (request.getQuantity() < 1) {
            throw new IllegalArgumentException("Quantity must be greater than 0");
        }

        User user = getCurrentUser(); // Lấy user hiện tại
        //Tìm Cart của User hiện tại đang ACTIVE (Nếu không có sẽ tạo Cart mới)
        Cart cart = cartRepository.findByUserAndStatus(user, CartStatus.ACTIVE).orElseGet(() -> {
            Cart newCart = new Cart();
            newCart.setUser(user);
            newCart.setStatus(CartStatus.ACTIVE);
            return cartRepository.save(newCart);
        });
        //Tìm sản phẩm theo productId trong CartItems
        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));

        //Tìm CartItem theo cartId và productId (nếu không có thì sẽ tạo CartItem mới)
        CartItem cartItem = cartItemRepository.findByCartAndProduct(cart, product).orElseGet(() -> {
            CartItem newItem = new CartItem();
            newItem.setCart(cart);
            newItem.setProduct(product);
            newItem.setQuantity(0);
            newItem.setUnitPrice(product.getPrice());
            newItem.setSubtotal(BigDecimal.ZERO);
            return newItem;
        });
        //Cập nhật lại Quantity mới (cũ + mới)
        int newQuantity = cartItem.getQuantity() + request.getQuantity();

        //Cập nhật Quantity, Price, Subtotal của Item
        cartItem.setQuantity(newQuantity);
        cartItem.setUnitPrice(product.getPrice());
        cartItem.setSubtotal(product.getPrice().multiply(BigDecimal.valueOf(newQuantity)));

        cartItemRepository.save(cartItem);

        return mapToCartResponse(cart);
    }

    @Transactional
    public CartResponse updateCartItem(Long cartItemId, UpdateCartRequest request) {
        if (cartItemId <= 0) {
            throw new IllegalArgumentException("Id must be greater than 0");
        }

        User user = getCurrentUser(); //Lấy user hiện tại

        //Tìm Cart của User hiện tại
        Cart cart = cartRepository.findByUserAndStatus(user, CartStatus.ACTIVE)
                .orElseThrow(() -> new ResourceNotFoundException("Active cart not found"));

        //Tìm Item trong Cart của User hiện tại
        CartItem cartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart item not found"));

        //so sánh cartId của item hiện tại với id của cart hiện tại có đúng không.
        if(!cartItem.getCart().getId().equals(cart.getId())){
            throw new BadRequestException("Cart item does not belong current user");
        }

        int newQuantity = request.getQuantity(); // Lấy Quantity mới cần cập nhật

        //Cập nhật lại Quantity mới, UnitPrice, Subtotal.
        cartItem.setQuantity(newQuantity);
        cartItem.setUnitPrice(cartItem.getProduct().getPrice());
        cartItem.setSubtotal(cartItem.getProduct().getPrice().multiply(BigDecimal.valueOf(newQuantity)));

        //Luư vào DB
        cartItemRepository.save(cartItem);

        return mapToCartResponse(cart);

    }

    @Transactional
    public CartResponse removeCartItem(Long cartItemId){
        if (cartItemId <= 0) {
            throw new IllegalArgumentException("Id must be greater than 0");
        }

        User user = getCurrentUser(); //Lấy user hiện tại

        //Tìm Cart của User hiện tại
        Cart cart = cartRepository.findByUserAndStatus(user, CartStatus.ACTIVE)
                .orElseThrow(() -> new ResourceNotFoundException("Active cart not found"));

        //Tìm Item trong Cart của User hiện tại
        CartItem cartItem = cartItemRepository.findByIdAndCart(cartItemId,cart)
                .orElseThrow(() -> new ResourceNotFoundException("Cart item not found"));

        if(!cartItem.getCart().getId().equals(cart.getId())){
            throw new BadRequestException("Cart item does not belong current user");
        }

        cartItemRepository.delete(cartItem);

        return mapToCartResponse(cart);
    }

    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if(authentication == null || !authentication.isAuthenticated()){
            throw new BadRequestException("Unauthenticated");
        }

        String username = authentication.getName();

        if("anonymousUser".equals(username)){
            throw new BadRequestException("Unauthenticated");
        }


        return userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }
}
