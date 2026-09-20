package com.adharsh.adharshmart.service;

import com.adharsh.adharshmart.dto.ReviewDTO;
import com.adharsh.adharshmart.exception.ValidationException;
import java.util.List;

/** F8 — product reviews and star ratings, restricted to completed (DELIVERED) orders. */
public interface ReviewService {
    ReviewDTO addReview(Long userId, Long productId, int rating, String comment) throws ValidationException;

    List<ReviewDTO> findByProduct(Long productId);
}
