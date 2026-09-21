package com.adharsh.adharshmart.dao;

import com.adharsh.adharshmart.model.ProductImage;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javax.sql.DataSource;

/** JDBC implementation of {@link ProductImageDAO}. Every statement is a PreparedStatement. */
public class ProductImageDAOImpl implements ProductImageDAO {

    private final DataSource dataSource;

    public ProductImageDAOImpl(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public List<ProductImage> findByProduct(Long productId) throws SQLException {
        String sql = "SELECT * FROM product_images WHERE product_id = ? ORDER BY sort_order";
        List<ProductImage> images = new ArrayList<>();
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, productId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    images.add(map(rs));
                }
            }
        }
        return images;
    }

    private ProductImage map(ResultSet rs) throws SQLException {
        ProductImage img = new ProductImage();
        img.setId(rs.getLong("id"));
        img.setProductId(rs.getLong("product_id"));
        img.setImageUrl(rs.getString("image_url"));
        img.setSortOrder(rs.getInt("sort_order"));
        return img;
    }
}
