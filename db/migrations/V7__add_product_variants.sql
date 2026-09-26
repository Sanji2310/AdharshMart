-- V7: size and color options per product, shown as selectable boxes/swatches on the product
-- detail page (F2/F3 UI). Purely descriptive/browsing metadata — stock stays tracked at the
-- product level, as it already is everywhere else in the app; selecting a size/color does not
-- affect stock_qty or the cart/order pipeline.
CREATE TABLE IF NOT EXISTS product_sizes (
    id         BIGINT AUTO_INCREMENT PRIMARY KEY,
    product_id BIGINT NOT NULL,
    label      VARCHAR(20) NOT NULL,
    sort_order INT NOT NULL DEFAULT 0,
    CONSTRAINT fk_product_sizes_product FOREIGN KEY (product_id) REFERENCES products (id)
);
CREATE INDEX IF NOT EXISTS idx_product_sizes_product_id ON product_sizes (product_id);

CREATE TABLE IF NOT EXISTS product_colors (
    id         BIGINT AUTO_INCREMENT PRIMARY KEY,
    product_id BIGINT NOT NULL,
    name       VARCHAR(40) NOT NULL,
    hex_code   VARCHAR(7) NOT NULL,
    sort_order INT NOT NULL DEFAULT 0,
    CONSTRAINT fk_product_colors_product FOREIGN KEY (product_id) REFERENCES products (id)
);
CREATE INDEX IF NOT EXISTS idx_product_colors_product_id ON product_colors (product_id);
