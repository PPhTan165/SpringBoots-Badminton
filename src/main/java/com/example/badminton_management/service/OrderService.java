package com.example.badminton_management.service;

import com.example.badminton_management.dto.order.CreateOrderRequest;
import com.example.badminton_management.dto.order.OrderItemResponse;
import com.example.badminton_management.dto.order.OrderResponse;
import com.example.badminton_management.dto.order.UpdateOrderStatusRequest;
import com.example.badminton_management.enums.CartStatus;
import com.example.badminton_management.enums.OrderStatus;
import com.example.badminton_management.exception.BadRequestException;
import com.example.badminton_management.exception.ResourceNotFoundException;
import com.example.badminton_management.jwt.CurrentUserHelper;
import com.example.badminton_management.model.*;
import com.example.badminton_management.repository.*;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class OrderService {
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final ProductRepository productRepository;
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final CurrentUserHelper currentUserHelper;

    public OrderService(
            OrderRepository orderRepository,
            OrderItemRepository orderItemRepository,
            ProductRepository productRepository,
            CartRepository cartRepository,
            CartItemRepository cartItemRepository,CurrentUserHelper currentUserHelper) {
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
        this.productRepository = productRepository;
        this.cartRepository = cartRepository;
        this.cartItemRepository = cartItemRepository;
        this.currentUserHelper = currentUserHelper;
    }

    private OrderItemResponse mapToOrderItemResponse(OrderItem orderItem){
        OrderItemResponse response = new OrderItemResponse();

        response.setId(orderItem.getId());
        response.setQuantity(orderItem.getQuantity());
        response.setUnitPrice(orderItem.getUnitPrice());
        response.setSubTotal(orderItem.getSubtotal());

        if(orderItem.getOrder() != null){
            response.setOrderId(orderItem.getOrder().getId());
            response.setOrderCode(orderItem.getOrder().getOrderCode());
        }

        if(orderItem.getProduct() != null){
            response.setProductId(orderItem.getProduct().getId());
            response.setProductName(orderItem.getProduct().getName());
        }

        return response;

    }

    private OrderResponse mapToOrderResponse(Order order){
        List<OrderItemResponse> items = orderItemRepository.findByOrder(order)
                .stream()
                .map(this::mapToOrderItemResponse)
                .toList();

        OrderResponse response = new OrderResponse();

        response.setId(order.getId());
        response.setOrderCode(order.getOrderCode());
        response.setTotalAmount(order.getTotalAmount());
        response.setOrderStatus(order.getOrderStatus().name());
        response.setShippingName(order.getShippingName());
        response.setShippingPhone(order.getShippingPhone());
        response.setShippingAddress(order.getShippingAddress());
        response.setNote(order.getNote());
        response.setItems(items);

        if (order.getUser() != null) {
            response.setUserId(order.getUser().getId());
            response.setUserName(order.getUser().getUsername());
        }

        return response;
    }
    //Lấy Order của User hiện tại
    public List<OrderResponse> getMyOrders(){
        User user = getCurrentUser();

       return orderRepository.findByUser(user)
                .stream()
                .map(this::mapToOrderResponse)
                .toList();
    }

    //Lấy Order chi tiết theo OrderId của User hiện tại
    public OrderResponse getMyOrderById(Long orderId){
        if(orderId <= 0){
            throw new IllegalArgumentException("Id must be greater than 0");
        }

        User user = getCurrentUser();
        Order order = orderRepository.findByIdAndUser(orderId, user)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + orderId));

        return mapToOrderResponse(order);
    }

    //Tạo Order
    @Transactional
    public OrderResponse createOrder(CreateOrderRequest request){
        User user = getCurrentUser();

        Cart cart = cartRepository.findByUserAndStatus(user, CartStatus.ACTIVE)
                .orElseThrow(()->new BadRequestException("Active cart not found"));

        List<CartItem> cartItems = cartItemRepository.findByCart(cart);

        if(cartItems.isEmpty()){
            throw new BadRequestException("Cart is empty");
        }

        for(CartItem cartItem : cartItems){
            Product product = cartItem.getProduct();

            if(cartItem.getQuantity() > product.getStockQuantity()){
                throw new BadRequestException("Product out of stock: "+ product.getName() );
            }
        }

        Order order = new Order();
        order.setUser(user);
        order.setOrderCode(generateOrderCode());
        order.setOrderStatus(OrderStatus.PENDING);
        order.setShippingName(request.getShippingName());
        order.setShippingPhone(request.getShippingPhone());
        order.setShippingAddress(request.getShippingAddress());
        order.setNote(request.getNote());

        BigDecimal totalAmount = cartItems.stream()
                .map(CartItem::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        order.setTotalAmount(totalAmount);

        Order savedOrder = orderRepository.save(order);

        for(CartItem cartItem: cartItems){
            OrderItem  orderItem = new OrderItem();
            orderItem.setOrder(savedOrder);
            orderItem.setProduct(cartItem.getProduct());
            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setUnitPrice(cartItem.getUnitPrice());
            orderItem.setSubtotal(cartItem.getSubtotal());

            orderItemRepository.save(orderItem);

            Product product = cartItem.getProduct();

            product.setStockQuantity(product.getStockQuantity() - cartItem.getQuantity());
            productRepository.save(product);
        }

        cartItemRepository.deleteAll(cartItems);

        return mapToOrderResponse(savedOrder);

    }

    public void cancelOrder(Long orderId){
        if(orderId <=0){
            throw new IllegalArgumentException("Id must be greater than 0");
        }
        Order order = orderRepository.findById(orderId)
                .orElseThrow(()->new BadRequestException("Order not found with id" + orderId));

        order.setOrderStatus(OrderStatus.CANCELLED);
    }

    //Lấy tất cả Orders (ADMIN)
    public List<OrderResponse> getAllOrders(){
        return  orderRepository.findAll()
                .stream().map(this::mapToOrderResponse).toList();
    }

    public OrderResponse getOrderById(Long orderId){
        if(orderId <=0){
            throw new IllegalArgumentException("Id must be greater than 0");
        }

        Order order = orderRepository.findById(orderId)
                .orElseThrow(()->new BadRequestException("Order not found with id" + orderId));
        if(order == null){
            throw new ResourceNotFoundException("Order not exists");
        }

        return mapToOrderResponse(order);
    }

    @Transactional
    public OrderResponse updateOrderStatus(Long orderId, UpdateOrderStatusRequest request){
        if (orderId <= 0) {
            throw new IllegalArgumentException("Id must be greater than 0");
        }

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + orderId));

        // Lấy trạng thái hiện tại của order và trạng thái mới client muốn cập nhật
        OrderStatus currentStatus = order.getOrderStatus();
        OrderStatus newStatus = request.getOrderStatus();

        // Nếu trạng thái mới giống trạng thái cũ thì không cần update, trả luôn response hiện tại
        if (currentStatus == newStatus) {
            return mapToOrderResponse(order);
        }

        // Chỉ cho phép chuyển trạng thái theo đúng luồng nghiệp vụ đã định nghĩa
        if (!isValidStatusTransition(currentStatus, newStatus)) {
            throw new BadRequestException(
                    "Cannot update order status from " + currentStatus + " to " + newStatus
            );
        }
        // Nếu order bị hủy thì hoàn lại số lượng sản phẩm vào kho
        // vì ở bước createOrder() đã trừ stock rồi
        if (newStatus == OrderStatus.CANCELLED) {
            List<OrderItem> orderItems = orderItemRepository.findByOrder(order);

            for (OrderItem orderItem : orderItems) {
                Product product = orderItem.getProduct();
                product.setStockQuantity(product.getStockQuantity() + orderItem.getQuantity());
                productRepository.save(product);
            }
        }
        // Cập nhật trạng thái mới cho order
        order.setOrderStatus(newStatus);
        Order updatedOrder = orderRepository.save(order);

        return mapToOrderResponse(updatedOrder);
    }

    private boolean isValidStatusTransition(OrderStatus currentStatus, OrderStatus newStatus) {
        return switch (currentStatus) {
            case PENDING -> newStatus == OrderStatus.CONFIRMED || newStatus == OrderStatus.CANCELLED;
            case CONFIRMED -> newStatus == OrderStatus.SHIPPING || newStatus == OrderStatus.CANCELLED;
            case SHIPPING -> newStatus == OrderStatus.COMPLETED;
            case COMPLETED, CANCELLED -> false;
        };
    }

    private User getCurrentUser(){
        return currentUserHelper.getCurrentUser();
    }
    private String generateOrderCode(){
        return "ORD-" + System.currentTimeMillis();
    }
}
