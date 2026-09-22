package com.adharsh.adharshmart.dao;

import com.adharsh.adharshmart.model.ProductSize;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javax.sql.DataSource;

/** JDBC implementation of {@link ProductSizeDAO}. Every statement is a PreparedStatement. */
public class ProductSizeDAOImpl implements ProductSizeDAO {

    private final DataSource dataSource;

    public ProductSizeDAOImpl(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public List<ProductSize> findByProduct(Long productId) throws SQLException {
        String sql = "SELECT * FROM product_sizes WHERE product_id = ? ORDER BY sort_order";
        List<ProductSize> sizes = new ArrayList<>();
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, productId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    sizes.add(map(rs));
                }
            }
        }
        return sizes;
    }

    private ProductSize map(ResultSet rs) throws SQLException {
        ProductSize size = new ProductSize();
        size.setId(rs.getLong("id"));
        size.setProductId(rs.getLong("product_id"));
        size.setLabel(rs.getString("label"));
        size.setSortOrder(rs.getInt("sort_order"));
        return size;
    }
}
