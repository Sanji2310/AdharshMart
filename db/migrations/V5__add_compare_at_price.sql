-- V5: sale pricing. compare_at_price is the pre-discount reference price; when set and greater
-- than price, the product is "on sale" (price is the current selling price either way).
ALTER TABLE products ADD COLUMN IF NOT EXISTS compare_at_price DECIMAL(10, 2);
ALTER TABLE products ADD CONSTRAINT IF NOT EXISTS chk_products_compare_at_price
    CHECK (compare_at_price IS NULL OR compare_at_price > price);
