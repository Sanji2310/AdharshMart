package com.adharsh.adharshmart.dao;

import com.adharsh.adharshmart.dto.SellerOrderItemDTO;
import com.adharsh.adharshmart.model.Order;
import com.adharsh.adharshmart.model.OrderItem;
import com.adharsh.adharshmart.model.OrderStatus;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

/**
 * Data access abstraction for {@code orders}/{@code order_items} (DAO pattern).
 * {@link #placeOrder} performs the order-header insert, line-item inserts, and stock decrement
 * as a single transaction — this is the D3 sequence diagram's Service -> DAO -> Database step.
 */
public interface OrderDAO {
    Order placeOrder(Order order, List<OrderItem> items) throws SQLException, InsufficientStockException;

    Optional<Order> findById(Long id) throws SQLException;

    List<Order> findByBuyer(Long buyerId) throws SQLException;

    List<OrderItem> findItemsByOrder(Long orderId) throws SQLException;

    List<SellerOrderItemDTO> findIncomingForSeller(Long sellerId) throws SQLException;

    void updateStatus(Long orderId, OrderStatus status) throws SQLException;

    List<Order> findAll() throws SQLException;

    /** F8 — reviews are only permitted on completed (DELIVERED) orders. */
    boolean hasDeliveredPurchase(Long buyerId, Long productId) throws SQLException;

    /** Raised when {@link #placeOrder} cannot reserve stock for a line item — mapped to HTTP 409. */
    class InsufficientStockException extends Exception {
        public InsufficientStockException(String message) {
            super(message);
        }
    }
}
