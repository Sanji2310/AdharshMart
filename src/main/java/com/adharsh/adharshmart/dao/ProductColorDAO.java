package com.adharsh.adharshmart.dao;

import com.adharsh.adharshmart.model.ProductColor;
import java.sql.SQLException;
import java.util.List;

/** Data access abstraction for the {@code product_colors} table (DAO pattern). */
public interface ProductColorDAO {
    List<ProductColor> findByProduct(Long productId) throws SQLException;
}
