package com.adharsh.adharshmart.dao;

import com.adharsh.adharshmart.model.ProductSize;
import java.sql.SQLException;
import java.util.List;

/** Data access abstraction for the {@code product_sizes} table (DAO pattern). */
public interface ProductSizeDAO {
    List<ProductSize> findByProduct(Long productId) throws SQLException;
}
