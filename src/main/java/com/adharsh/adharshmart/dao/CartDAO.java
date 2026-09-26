package com.adharsh.adharshmart.dao;

import com.adharsh.adharshmart.model.CartItem;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

/** Data access abstraction for the {@code cart_items} table (DAO pattern). */
public interface CartDAO {
    Optional<CartItem> findByUserAndProduct(Long userId, Long productId) throws SQLException;

    CartItem create(CartItem item) throws SQLException;

    void updateQuantity(Long id, int quantity) throws SQLException;

    void remove(Long id, Long userId) throws SQLException;

    List<CartItem> findByUser(Long userId) throws SQLException;

    void clearByUser(Long userId) throws SQLException;
}
