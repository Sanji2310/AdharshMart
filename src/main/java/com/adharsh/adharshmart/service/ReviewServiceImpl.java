package com.adharsh.adharshmart.service;

import com.adharsh.adharshmart.dao.OrderDAO;
import com.adharsh.adharshmart.dao.ReviewDAO;
import com.adharsh.adharshmart.dao.UserDAO;
import com.adharsh.adharshmart.dto.ReviewDTO;
import com.adharsh.adharshmart.exception.DataAccessException;
import com.adharsh.adharshmart.exception.ValidationException;
import com.adharsh.adharshmart.model.Review;
import com.adharsh.adharshmart.util.ValidationUtil;
import java.sql.SQLException;
import java.util.List;

/** Business rules for F8 — depends on DAO interfaces only. */
public class ReviewServiceImpl implements ReviewService {

    private final ReviewDAO reviewDAO;
    private final OrderDAO orderDAO;
    private final UserDAO userDAO;

    public ReviewServiceImpl(ReviewDAO reviewDAO, OrderDAO orderDAO, UserDAO userDAO) {
        this.reviewDAO = reviewDAO;
        this.orderDAO = orderDAO;
        this.userDAO = userDAO;
    }

    @Override
    public ReviewDTO addReview(Long userId, Long productId, int rating, String comment) throws ValidationException {
        if (!ValidationUtil.isValidRating(rating)) {
            throw new ValidationException("rating", "VALIDATION_ERROR", "Rating must be between 1 and 5");
        }
        try {
            if (!orderDAO.hasDeliveredPurchase(userId, productId)) {
                throw new ValidationException("productId", "NOT_ELIGIBLE",
                        "You can only review products from a delivered order");
            }
            if (reviewDAO.existsByUserAndProduct(userId, productId)) {
                throw new ValidationException("productId", "ALREADY_REVIEWED", "You already reviewed this product");
            }
            Review review = new Review();
            review.setUserId(userId);
            review.setProductId(productId);
            review.setRating(rating);
            review.setComment(comment);
            Review created = reviewDAO.create(review);
            String reviewerName = userDAO.findById(userId).map(u -> u.getName()).orElse("Anonymous");
            return ReviewDTO.from(created, reviewerName);
        } catch (SQLException e) {
            throw new DataAccessException("Failed to add review", e);
        }
    }

    @Override
    public List<ReviewDTO> findByProduct(Long productId) {
        try {
            List<Review> reviews = reviewDAO.findByProduct(productId);
            List<ReviewDTO> result = new java.util.ArrayList<>();
            for (Review r : reviews) {
                String reviewerName = userDAO.findById(r.getUserId()).map(u -> u.getName()).orElse("Anonymous");
                result.add(ReviewDTO.from(r, reviewerName));
            }
            return result;
        } catch (SQLException e) {
            throw new DataAccessException("Failed to load reviews", e);
        }
    }
}
