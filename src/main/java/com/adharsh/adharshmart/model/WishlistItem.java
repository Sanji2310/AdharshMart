package com.adharsh.adharshmart.model;

import java.time.LocalDateTime;

/** Maps to the {@code wishlist_items} table (O1 — wishlist / save-for-later). */
public class WishlistItem {
    private Long id;
    private Long userId;
    private Long productId;
    private LocalDateTime createdAt;

    public WishlistItem() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
