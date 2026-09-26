package com.adharsh.adharshmart.dto;

import com.adharsh.adharshmart.model.OrderStatus;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Composite response shape for an order plus its line items (F5, F6).
 * Built via {@link Builder} — the coding-standard mandated Builder pattern application.
 */
public final class OrderResponseDTO {
    private final Long id;
    private final Long buyerId;
    private final OrderStatus status;
    private final BigDecimal totalAmount;
    private final LocalDateTime createdAt;
    private final List<OrderItemDTO> items;

    private OrderResponseDTO(Builder b) {
        this.id = b.id;
        this.buyerId = b.buyerId;
        this.status = b.status;
        this.totalAmount = b.totalAmount;
        this.createdAt = b.createdAt;
        this.items = Collections.unmodifiableList(b.items);
    }

    public static Builder builder() {
        return new Builder();
    }

    public Long getId() {
        return id;
    }

    public Long getBuyerId() {
        return buyerId;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public List<OrderItemDTO> getItems() {
        return items;
    }

    /** Fluent builder — assembling an order response requires an id, buyer, status, total, and a variable-length item list. */
    public static final class Builder {
        private Long id;
        private Long buyerId;
        private OrderStatus status;
        private BigDecimal totalAmount;
        private LocalDateTime createdAt;
        private final List<OrderItemDTO> items = new ArrayList<>();

        public Builder id(Long id) {
            this.id = id;
            return this;
        }

        public Builder buyerId(Long buyerId) {
            this.buyerId = buyerId;
            return this;
        }

        public Builder status(OrderStatus status) {
            this.status = status;
            return this;
        }

        public Builder totalAmount(BigDecimal totalAmount) {
            this.totalAmount = totalAmount;
            return this;
        }

        public Builder createdAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public Builder addItem(OrderItemDTO item) {
            this.items.add(item);
            return this;
        }

        public Builder items(List<OrderItemDTO> items) {
            this.items.clear();
            this.items.addAll(items);
            return this;
        }

        public OrderResponseDTO build() {
            return new OrderResponseDTO(this);
        }
    }

    /** A single line item within an order response. */
    public static final class OrderItemDTO {
        private final Long productId;
        private final String productName;
        private final int quantity;
        private final BigDecimal unitPrice;

        public OrderItemDTO(Long productId, String productName, int quantity, BigDecimal unitPrice) {
            this.productId = productId;
            this.productName = productName;
            this.quantity = quantity;
            this.unitPrice = unitPrice;
        }

        public Long getProductId() {
            return productId;
        }

        public String getProductName() {
            return productName;
        }

        public int getQuantity() {
            return quantity;
        }

        public BigDecimal getUnitPrice() {
            return unitPrice;
        }
    }
}
