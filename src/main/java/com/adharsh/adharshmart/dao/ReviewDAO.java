package com.adharsh.adharshmart.dao;

import com.adharsh.adharshmart.model.Review;
import java.sql.SQLException;
import java.util.List;

/** Data access abstraction for the {@code reviews} table (DAO pattern). */
public interface ReviewDAO {
    Review create(Review review) throws SQLException;

    List<Review> findByProduct(Long productId) throws SQLException;

    boolean existsByUserAndProduct(Long userId, Long productId) throws SQLException;

    double averageRating(Long productId) throws SQLException;

    int countForProduct(Long productId) throws SQLException;
}
