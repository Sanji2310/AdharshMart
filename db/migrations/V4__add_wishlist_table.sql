-- V4: wishlist / save-for-later (O1)
CREATE TABLE wishlist_items (
    id         BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id    BIGINT NOT NULL,
    product_id BIGINT NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_wishlist_items_user FOREIGN KEY (user_id) REFERENCES users (id),
    CONSTRAINT fk_wishlist_items_product FOREIGN KEY (product_id) REFERENCES products (id),
    CONSTRAINT uq_wishlist_user_product UNIQUE (user_id, product_id)
);
CREATE INDEX idx_wishlist_items_user_id ON wishlist_items (user_id);
CREATE INDEX idx_wishlist_items_product_id ON wishlist_items (product_id);
