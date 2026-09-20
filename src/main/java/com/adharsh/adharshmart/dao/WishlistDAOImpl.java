package com.adharsh.adharshmart.dao;

import com.adharsh.adharshmart.model.WishlistItem;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import javax.sql.DataSource;

/** JDBC implementation of {@link WishlistDAO}. Every statement is a PreparedStatement. */
public class WishlistDAOImpl implements WishlistDAO {

    private final DataSource dataSource;

    public WishlistDAOImpl(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public WishlistItem add(Long userId, Long productId) throws SQLException {
        String sql = "INSERT INTO wishlist_items (user_id, product_id, created_at) VALUES (?, ?, ?)";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setLong(1, userId);
            ps.setLong(2, productId);
            ps.setTimestamp(3, Timestamp.valueOf(java.time.LocalDateTime.now()));
            ps.executeUpdate();
            WishlistItem item = new WishlistItem();
            item.setUserId(userId);
            item.setProductId(productId);
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    item.setId(keys.getLong(1));
                }
            }
            return item;
        }
    }

    @Override
    public void remove(Long id, Long userId) throws SQLException {
        String sql = "DELETE FROM wishlist_items WHERE id = ? AND user_id = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, id);
            ps.setLong(2, userId);
            ps.executeUpdate();
        }
    }

    @Override
    public boolean existsByUserAndProduct(Long userId, Long productId) throws SQLException {
        String sql = "SELECT 1 FROM wishlist_items WHERE user_id = ? AND product_id = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, userId);
            ps.setLong(2, productId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    @Override
    public List<WishlistItem> findByUser(Long userId) throws SQLException {
        String sql = "SELECT * FROM wishlist_items WHERE user_id = ? ORDER BY created_at DESC";
        List<WishlistItem> items = new ArrayList<>();
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    WishlistItem item = new WishlistItem();
                    item.setId(rs.getLong("id"));
                    item.setUserId(rs.getLong("user_id"));
                    item.setProductId(rs.getLong("product_id"));
                    item.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
                    items.add(item);
                }
            }
        }
        return items;
    }
}
