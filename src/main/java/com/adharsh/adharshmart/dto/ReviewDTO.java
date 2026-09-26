package com.adharsh.adharshmart.dto;

import com.adharsh.adharshmart.model.Review;
import java.time.LocalDateTime;

/** Response shape for product reviews (F8). */
public class ReviewDTO {
    private Long id;
    private Long productId;
    private Long userId;
    private String reviewerName;
    private int rating;
    private String comment;
    private LocalDateTime createdAt;

    public static ReviewDTO from(Review r, String reviewerName) {
        ReviewDTO dto = new ReviewDTO();
        dto.id = r.getId();
        dto.productId = r.getProductId();
        dto.userId = r.getUserId();
        dto.reviewerName = reviewerName;
        dto.rating = r.getRating();
        dto.comment = r.getComment();
        dto.createdAt = r.getCreatedAt();
        return dto;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getReviewerName() {
        return reviewerName;
    }

    public void setReviewerName(String reviewerName) {
        this.reviewerName = reviewerName;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
