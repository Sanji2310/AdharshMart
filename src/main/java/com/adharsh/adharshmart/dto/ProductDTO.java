package com.adharsh.adharshmart.dto;

import com.adharsh.adharshmart.model.Product;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/** Request/response shape for product listings (F2, F3). */
public class ProductDTO {
    private Long id;
    private Long sellerId;
    private String sellerName;
    private String name;
    private String description;
    private BigDecimal price;
    private int stockQty;
    private String category;
    private String imageUrl;
    private boolean active;
    private LocalDateTime createdAt;
    private double averageRating;
    private int reviewCount;

    public ProductDTO() {
    }

    public static ProductDTO from(Product p) {
        ProductDTO dto = new ProductDTO();
        dto.setId(p.getId());
        dto.setSellerId(p.getSellerId());
        dto.setName(p.getName());
        dto.setDescription(p.getDescription());
        dto.setPrice(p.getPrice());
        dto.setStockQty(p.getStockQty());
        dto.setCategory(p.getCategory());
        dto.setImageUrl(p.getImageUrl());
        dto.setActive(p.isActive());
        dto.setCreatedAt(p.getCreatedAt());
        return dto;
    }

    public Product toEntity() {
        Product p = new Product();
        p.setId(id);
        p.setSellerId(sellerId);
        p.setName(name);
        p.setDescription(description);
        p.setPrice(price);
        p.setStockQty(stockQty);
        p.setCategory(category);
        p.setImageUrl(imageUrl);
        p.setActive(active);
        return p;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getSellerId() {
        return sellerId;
    }

    public void setSellerId(Long sellerId) {
        this.sellerId = sellerId;
    }

    /** Populated only by the admin marketplace-wide listing (F7) — null on every other response. */
    public String getSellerName() {
        return sellerName;
    }

    public void setSellerName(String sellerName) {
        this.sellerName = sellerName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public int getStockQty() {
        return stockQty;
    }

    public void setStockQty(int stockQty) {
        this.stockQty = stockQty;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public double getAverageRating() {
        return averageRating;
    }

    public void setAverageRating(double averageRating) {
        this.averageRating = averageRating;
    }

    public int getReviewCount() {
        return reviewCount;
    }

    public void setReviewCount(int reviewCount) {
        this.reviewCount = reviewCount;
    }
}
