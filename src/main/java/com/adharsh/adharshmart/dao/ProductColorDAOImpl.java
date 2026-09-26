package com.adharsh.adharshmart.dao;

import com.adharsh.adharshmart.model.ProductColor;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javax.sql.DataSource;

/** JDBC implementation of {@link ProductColorDAO}. Every statement is a PreparedStatement. */
public class ProductColorDAOImpl implements ProductColorDAO {

    private final DataSource dataSource;

    public ProductColorDAOImpl(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public List<ProductColor> findByProduct(Long productId) throws SQLException {
        String sql = "SELECT * FROM product_colors WHERE product_id = ? ORDER BY sort_order";
        List<ProductColor> colors = new ArrayList<>();
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, productId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    colors.add(map(rs));
                }
            }
        }
        return colors;
    }

    private ProductColor map(ResultSet rs) throws SQLException {
        ProductColor color = new ProductColor();
        color.setId(rs.getLong("id"));
        color.setProductId(rs.getLong("product_id"));
        color.setName(rs.getString("name"));
        color.setHexCode(rs.getString("hex_code"));
        color.setSortOrder(rs.getInt("sort_order"));
        return color;
    }
}
