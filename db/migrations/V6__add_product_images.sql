-- V6: per-product image gallery. products.image_url remains the primary/cover shot used
-- everywhere a single thumbnail is needed; this table adds the extra angles shown on the
-- product detail page's thumbnail strip.
CREATE TABLE IF NOT EXISTS product_images (
    id         BIGINT AUTO_INCREMENT PRIMARY KEY,
    product_id BIGINT NOT NULL,
    image_url  VARCHAR(500) NOT NULL,
    sort_order INT NOT NULL DEFAULT 0,
    CONSTRAINT fk_product_images_product FOREIGN KEY (product_id) REFERENCES products (id)
);
CREATE INDEX IF NOT EXISTS idx_product_images_product_id ON product_images (product_id);
