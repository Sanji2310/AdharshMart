package com.adharsh.adharshmart.dao;

import com.adharsh.adharshmart.model.Product;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

/** Data access abstraction for the {@code products} table (DAO pattern). */
public interface ProductDAO {
    Product create(Product product) throws SQLException;

    void update(Product product) throws SQLException;

    void delete(Long id) throws SQLException;

    Optional<Product> findById(Long id) throws SQLException;

    List<Product> findBySeller(Long sellerId) throws SQLException;

    List<Product> search(String keyword, String category) throws SQLException;

    List<Product> findAllActive() throws SQLException;

    boolean decrementStock(Long productId, int quantity) throws SQLException;
}
