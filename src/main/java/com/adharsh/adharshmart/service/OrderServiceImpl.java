package com.adharsh.adharshmart.service;

import com.adharsh.adharshmart.dao.CartDAO;
import com.adharsh.adharshmart.dao.OrderDAO;
import com.adharsh.adharshmart.dto.OrderResponseDTO;
import com.adharsh.adharshmart.dto.SellerOrderItemDTO;
import com.adharsh.adharshmart.exception.DataAccessException;
import com.adharsh.adharshmart.exception.NotFoundException;
import com.adharsh.adharshmart.exception.UnauthorizedException;
import com.adharsh.adharshmart.exception.ValidationException;
import com.adharsh.adharshmart.model.CartItem;
import com.adharsh.adharshmart.model.Order;
import com.adharsh.adharshmart.model.OrderItem;
import com.adharsh.adharshmart.model.OrderStatus;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/** Business rules for F5/F6/O2 — depends only on DAO interfaces and the {@link PaymentStrategy}. */
public class OrderServiceImpl implements OrderService {

    private static final Map<OrderStatus, Set<OrderStatus>> ALLOWED_TRANSITIONS = new EnumMap<>(OrderStatus.class);

    static {
        ALLOWED_TRANSITIONS.put(OrderStatus.PENDING, EnumSet.of(OrderStatus.CONFIRMED, OrderStatus.CANCELLED));
        ALLOWED_TRANSITIONS.put(OrderStatus.CONFIRMED, EnumSet.of(OrderStatus.SHIPPED, OrderStatus.CANCELLED));
        ALLOWED_TRANSITIONS.put(OrderStatus.SHIPPED, EnumSet.of(OrderStatus.DELIVERED));
        ALLOWED_TRANSITIONS.put(OrderStatus.DELIVERED, EnumSet.noneOf(OrderStatus.class));
        ALLOWED_TRANSITIONS.put(OrderStatus.CANCELLED, EnumSet.noneOf(OrderStatus.class));
    }

    private final OrderDAO orderDAO;
    private final CartDAO cartDAO;
    private final com.adharsh.adharshmart.dao.ProductDAO productDAO;
    private final PaymentStrategy paymentStrategy;

    public OrderServiceImpl(OrderDAO orderDAO, CartDAO cartDAO, com.adharsh.adharshmart.dao.ProductDAO productDAO,
                             PaymentStrategy paymentStrategy) {
        this.orderDAO = orderDAO;
        this.cartDAO = cartDAO;
        this.productDAO = productDAO;
        this.paymentStrategy = paymentStrategy;
    }

    @Override
    public OrderResponseDTO checkout(Long buyerId) throws ValidationException {
        try {
            List<CartItem> cartItems = cartDAO.findByUser(buyerId);
            if (cartItems.isEmpty()) {
                throw new ValidationException("cart", "EMPTY_CART", "Your cart is empty");
            }

            BigDecimal total = BigDecimal.ZERO;
            List<OrderItem> orderItems = new java.util.ArrayList<>();
            for (CartItem ci : cartItems) {
                var product = productDAO.findById(ci.getProductId()).orElse(null);
                if (product == null || !product.isActive()) {
                    throw new ValidationException("cart", "PRODUCT_UNAVAILABLE",
                            "A product in your cart is no longer available");
                }
                OrderItem oi = new OrderItem();
                oi.setProductId(product.getId());
                oi.setQuantity(ci.getQuantity());
                oi.setUnitPrice(product.getPrice());
                orderItems.add(oi);
                total = total.add(product.getPrice().multiply(BigDecimal.valueOf(ci.getQuantity())));
            }

            PaymentStrategy.PaymentResult payment = paymentStrategy.confirm(total);
            if (!payment.approved()) {
                throw new ValidationException("payment", "PAYMENT_DECLINED", "Mock payment was not confirmed");
            }

            Order order = new Order();
            order.setBuyerId(buyerId);
            order.setStatus(OrderStatus.CONFIRMED);
            order.setTotalAmount(total);

            Order placed = orderDAO.placeOrder(order, orderItems);
            cartDAO.clearByUser(buyerId);
            return toResponse(placed, orderDAO.findItemsByOrder(placed.getId()));
        } catch (OrderDAO.InsufficientStockException e) {
            throw new ValidationException("cart", "OUT_OF_STOCK", e.getMessage());
        } catch (SQLException e) {
            throw new DataAccessException("Checkout failed", e);
        }
    }

    @Override
    public OrderResponseDTO getOrder(Long orderId, Long requesterId, boolean isAdmin)
            throws NotFoundException, UnauthorizedException {
        try {
            Order order = orderDAO.findById(orderId).orElseThrow(() -> new NotFoundException("Order not found"));
            if (!isAdmin && !order.getBuyerId().equals(requesterId)) {
                throw new UnauthorizedException("You do not have access to this order", true);
            }
            return toResponse(order, orderDAO.findItemsByOrder(orderId));
        } catch (SQLException e) {
            throw new DataAccessException("Failed to load order", e);
        }
    }

    @Override
    public List<OrderResponseDTO> findByBuyer(Long buyerId) {
        try {
            List<Order> orders = orderDAO.findByBuyer(buyerId);
            List<OrderResponseDTO> result = new java.util.ArrayList<>();
            for (Order order : orders) {
                result.add(toResponse(order, orderDAO.findItemsByOrder(order.getId())));
            }
            return result;
        } catch (SQLException e) {
            throw new DataAccessException("Failed to load order history", e);
        }
    }

    @Override
    public List<SellerOrderItemDTO> findIncomingForSeller(Long sellerId) {
        try {
            return orderDAO.findIncomingForSeller(sellerId);
        } catch (SQLException e) {
            throw new DataAccessException("Failed to load incoming orders", e);
        }
    }

    @Override
    public void updateStatus(Long orderId, OrderStatus next) throws ValidationException {
        try {
            Order order = orderDAO.findById(orderId)
                    .orElseThrow(() -> new IllegalArgumentException("Order not found"));
            Set<OrderStatus> allowed = ALLOWED_TRANSITIONS.getOrDefault(order.getStatus(), Set.of());
            if (!allowed.contains(next)) {
                throw new ValidationException("status", "INVALID_TRANSITION",
                        "Cannot move an order from " + order.getStatus() + " to " + next);
            }
            orderDAO.updateStatus(orderId, next);
        } catch (SQLException e) {
            throw new DataAccessException("Failed to update order status", e);
        }
    }

    @Override
    public List<OrderResponseDTO> findAll() {
        try {
            List<Order> orders = orderDAO.findAll();
            List<OrderResponseDTO> result = new java.util.ArrayList<>();
            for (Order order : orders) {
                result.add(toResponse(order, orderDAO.findItemsByOrder(order.getId())));
            }
            return result;
        } catch (SQLException e) {
            throw new DataAccessException("Failed to load orders", e);
        }
    }

    private OrderResponseDTO toResponse(Order order, List<OrderItem> items) {
        OrderResponseDTO.Builder builder = OrderResponseDTO.builder()
                .id(order.getId())
                .buyerId(order.getBuyerId())
                .status(order.getStatus())
                .totalAmount(order.getTotalAmount())
                .createdAt(order.getCreatedAt());
        builder.items(items.stream()
                .map(i -> new OrderResponseDTO.OrderItemDTO(i.getProductId(), i.getProductName(), i.getQuantity(), i.getUnitPrice()))
                .collect(Collectors.toList()));
        return builder.build();
    }
}
