package com.adharsh.adharshmart.dao;

import com.adharsh.adharshmart.model.WishlistItem;
import java.sql.SQLException;
import java.util.List;

/** Data access abstraction for the {@code wishlist_items} table (DAO pattern, O1). */
public interface WishlistDAO {
    WishlistItem add(Long userId, Long productId) throws SQLException;

    void remove(Long id, Long userId) throws SQLException;

    boolean existsByUserAndProduct(Long userId, Long productId) throws SQLException;

    List<WishlistItem> findByUser(Long userId) throws SQLException;
}
