package com.adharsh.adharshmart.dao;

import com.adharsh.adharshmart.model.ProductImage;
import java.sql.SQLException;
import java.util.List;

/** Data access abstraction for the {@code product_images} table (DAO pattern). */
public interface ProductImageDAO {
    List<ProductImage> findByProduct(Long productId) throws SQLException;
}
