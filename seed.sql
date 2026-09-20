-- AdharshMart seed data
-- Seed passwords (dev/demo only — rotate before any real deployment):
--   admin@adharshmart.com  / AdminPass123!
--   seller@adharshmart.com / SellerPass123!
--   buyer@adharshmart.com  / BuyerPass123!

MERGE INTO users (id, name, email, password_hash, role, created_at) KEY (id) VALUES
  (1, 'Adharsh Admin',  'admin@adharshmart.com',  '$2a$10$PMsQuJY9QcAXpi0RkPGR3e295M4okUkfPabdQwriM.DSSQnU/cFLS', 'ADMIN',  CURRENT_TIMESTAMP),
  (2, 'Nora Vale',      'seller@adharshmart.com', '$2a$10$qSCKO9E7P/l4rfSjSbrxqOQonS6UgbXpKuGzL22IgERUSGQx/cG8m', 'SELLER', CURRENT_TIMESTAMP),
  (3, 'Priya Buyer',    'buyer@adharshmart.com',  '$2a$10$psvsWHdZb9gxpVpE26rfgeih35Eh9Ui7PXwrJ8KLhhQwd4Gco.Q9W', 'BUYER',  CURRENT_TIMESTAMP);

ALTER TABLE users ALTER COLUMN id RESTART WITH 4;

-- Product imagery: picsum.photos seed-based URLs. Deterministic (same seed -> same
-- image on every request) and guaranteed to resolve — no guessed Unsplash photo IDs.
-- Swap for real product photography whenever that's available (see README §8).
MERGE INTO products (id, seller_id, name, description, price, stock_qty, category, image_url, active, created_at) KEY (id) VALUES
  (1, 2, 'Atelier Wool Overcoat', 'Double-faced wool overcoat, hand-finished seams, tonal horn buttons.', 890.00, 12, 'Outerwear', 'https://picsum.photos/seed/adharshmart-wool-overcoat/900/1125', TRUE, CURRENT_TIMESTAMP),
  (2, 2, 'Silk Column Dress',     'Bias-cut silk charmeuse column dress in ink black.', 620.00, 8, 'Dresses', 'https://picsum.photos/seed/adharshmart-silk-dress/900/1125', TRUE, CURRENT_TIMESTAMP),
  (3, 2, 'Leather Structured Tote', 'Vegetable-tanned calfskin tote with brushed brass hardware.', 1150.00, 6, 'Bags', 'https://picsum.photos/seed/adharshmart-leather-tote/900/1125', TRUE, CURRENT_TIMESTAMP),
  (4, 2, 'Cashmere Crewneck', 'Pure Mongolian cashmere crewneck, seamless knit.', 340.00, 20, 'Knitwear', 'https://picsum.photos/seed/adharshmart-cashmere-crewneck/900/1125', TRUE, CURRENT_TIMESTAMP),
  (5, 2, 'Court Sneaker — Blanc', 'Minimalist leather court sneaker with vulcanized sole.', 410.00, 25, 'Footwear', 'https://picsum.photos/seed/adharshmart-court-sneaker/900/1125', TRUE, CURRENT_TIMESTAMP),
  (6, 2, 'Tailored Wool Trouser', 'High-rise straight-leg trouser in Italian wool twill.', 295.00, 18, 'Trousers', 'https://picsum.photos/seed/adharshmart-wool-trouser/900/1125', TRUE, CURRENT_TIMESTAMP),
  (7, 2, 'Signature Aviator Sunglasses', 'Titanium frame, gradient polarized lens.', 265.00, 30, 'Accessories', 'https://picsum.photos/seed/adharshmart-aviator-sunglasses/900/1125', TRUE, CURRENT_TIMESTAMP),
  (8, 2, 'Performance Runner — Volt', 'Engineered knit upper, responsive foam midsole.', 175.00, 40, 'Footwear', 'https://picsum.photos/seed/adharshmart-performance-runner/900/1125', TRUE, CURRENT_TIMESTAMP);

ALTER TABLE products ALTER COLUMN id RESTART WITH 9;

MERGE INTO reviews (id, product_id, user_id, rating, comment, created_at) KEY (id) VALUES
  (1, 1, 3, 5, 'Impeccable tailoring, worth every rupee.', CURRENT_TIMESTAMP),
  (2, 5, 3, 4, 'Fits true to size, très chic.', CURRENT_TIMESTAMP);

ALTER TABLE reviews ALTER COLUMN id RESTART WITH 3;
