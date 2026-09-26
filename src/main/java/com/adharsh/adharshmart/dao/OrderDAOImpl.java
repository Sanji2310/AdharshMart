package com.adharsh.adharshmart.dao;

import com.adharsh.adharshmart.dto.SellerOrderItemDTO;
import com.adharsh.adharshmart.model.Order;
import com.adharsh.adharshmart.model.OrderItem;
import com.adharsh.adharshmart.model.OrderStatus;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import javax.sql.DataSource;

/** JDBC implementation of {@link OrderDAO}. Every statement is a PreparedStatement. */
public class OrderDAOImpl implements OrderDAO {

    private final DataSource dataSource;

    public OrderDAOImpl(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public Order placeOrder(Order order, List<OrderItem> items) throws SQLException, InsufficientStockException {
        String insertOrder = "INSERT INTO orders (buyer_id, status, total_amount, created_at) VALUES (?, ?, ?, ?)";
        String insertItem = "INSERT INTO order_items (order_id, product_id, quantity, unit_price, created_at) "
                + "VALUES (?, ?, ?, ?, ?)";
        String decrementStock = "UPDATE products SET stock_qty = stock_qty - ? WHERE id = ? AND stock_qty >= ?";

        try (Connection conn = dataSource.getConnection()) {
            conn.setAutoCommit(false);
            try {
                Long orderId;
                try (PreparedStatement ps = conn.prepareStatement(insertOrder, Statement.RETURN_GENERATED_KEYS)) {
                    ps.setLong(1, order.getBuyerId());
                    ps.setString(2, order.getStatus().name());
                    ps.setBigDecimal(3, order.getTotalAmount());
                    ps.setTimestamp(4, Timestamp.valueOf(java.time.LocalDateTime.now()));
                    ps.executeUpdate();
                    try (ResultSet keys = ps.getGeneratedKeys()) {
                        keys.next();
                        orderId = keys.getLong(1);
                    }
                }
                order.setId(orderId);

                for (OrderItem item : items) {
                    try (PreparedStatement stockPs = conn.prepareStatement(decrementStock)) {
                        stockPs.setInt(1, item.getQuantity());
                        stockPs.setLong(2, item.getProductId());
                        stockPs.setInt(3, item.getQuantity());
                        if (stockPs.executeUpdate() != 1) {
                            throw new InsufficientStockException(
                                    "Insufficient stock for product id " + item.getProductId());
                        }
                    }
                    try (PreparedStatement itemPs = conn.prepareStatement(insertItem)) {
                        itemPs.setLong(1, orderId);
                        itemPs.setLong(2, item.getProductId());
                        itemPs.setInt(3, item.getQuantity());
                        itemPs.setBigDecimal(4, item.getUnitPrice());
                        itemPs.setTimestamp(5, Timestamp.valueOf(java.time.LocalDateTime.now()));
                        itemPs.executeUpdate();
                    }
                }
                conn.commit();
                return order;
            } catch (InsufficientStockException e) {
                conn.rollback();
                throw e;
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            } finally {
                conn.setAutoCommit(true);
            }
        }
    }

    @Override
    public Optional<Order> findById(Long id) throws SQLException {
        String sql = "SELECT * FROM orders WHERE id = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? Optional.of(mapOrder(rs)) : Optional.empty();
            }
        }
    }

    @Override
    public List<Order> findByBuyer(Long buyerId) throws SQLException {
        String sql = "SELECT * FROM orders WHERE buyer_id = ? ORDER BY created_at DESC";
        List<Order> orders = new ArrayList<>();
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, buyerId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    orders.add(mapOrder(rs));
                }
            }
        }
        return orders;
    }

    @Override
    public List<OrderItem> findItemsByOrder(Long orderId) throws SQLException {
        String sql = "SELECT oi.*, p.name AS product_name FROM order_items oi "
                + "JOIN products p ON p.id = oi.product_id WHERE oi.order_id = ?";
        List<OrderItem> items = new ArrayList<>();
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, orderId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    OrderItem item = new OrderItem();
                    item.setId(rs.getLong("id"));
                    item.setOrderId(rs.getLong("order_id"));
                    item.setProductId(rs.getLong("product_id"));
                    item.setQuantity(rs.getInt("quantity"));
                    item.setUnitPrice(rs.getBigDecimal("unit_price"));
                    item.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
                    item.setProductName(rs.getString("product_name"));
                    items.add(item);
                }
            }
        }
        return items;
    }

    @Override
    public List<SellerOrderItemDTO> findIncomingForSeller(Long sellerId) throws SQLException {
        String sql = "SELECT o.id AS order_id, o.status, o.created_at, u.name AS buyer_name, "
                + "p.id AS product_id, p.name AS product_name, oi.quantity, oi.unit_price "
                + "FROM order_items oi "
                + "JOIN orders o ON o.id = oi.order_id "
                + "JOIN products p ON p.id = oi.product_id "
                + "JOIN users u ON u.id = o.buyer_id "
                + "WHERE p.seller_id = ? ORDER BY o.created_at DESC";
        List<SellerOrderItemDTO> results = new ArrayList<>();
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, sellerId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    SellerOrderItemDTO dto = new SellerOrderItemDTO();
                    dto.setOrderId(rs.getLong("order_id"));
                    dto.setStatus(OrderStatus.valueOf(rs.getString("status")));
                    dto.setBuyerName(rs.getString("buyer_name"));
                    dto.setProductId(rs.getLong("product_id"));
                    dto.setProductName(rs.getString("product_name"));
                    dto.setQuantity(rs.getInt("quantity"));
                    dto.setUnitPrice(rs.getBigDecimal("unit_price"));
                    dto.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
                    results.add(dto);
                }
            }
        }
        return results;
    }

    @Override
    public void updateStatus(Long orderId, OrderStatus status) throws SQLException {
        String sql = "UPDATE orders SET status = ? WHERE id = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, status.name());
            ps.setLong(2, orderId);
            ps.executeUpdate();
        }
    }

    @Override
    public List<Order> findAll() throws SQLException {
        String sql = "SELECT * FROM orders ORDER BY created_at DESC";
        List<Order> orders = new ArrayList<>();
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                orders.add(mapOrder(rs));
            }
        }
        return orders;
    }

    @Override
    public boolean hasDeliveredPurchase(Long buyerId, Long productId) throws SQLException {
        String sql = "SELECT 1 FROM orders o JOIN order_items oi ON oi.order_id = o.id "
                + "WHERE o.buyer_id = ? AND oi.product_id = ? AND o.status = 'DELIVERED'";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, buyerId);
            ps.setLong(2, productId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    private Order mapOrder(ResultSet rs) throws SQLException {
        Order order = new Order();
        order.setId(rs.getLong("id"));
        order.setBuyerId(rs.getLong("buyer_id"));
        order.setStatus(OrderStatus.valueOf(rs.getString("status")));
        order.setTotalAmount(rs.getBigDecimal("total_amount"));
        order.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
        return order;
    }
}
