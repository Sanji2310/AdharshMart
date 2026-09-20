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

-- Product imagery: real photos sourced live via the Unsplash API (MCP connector), matched by
-- search query per product — not guessed IDs. Category, not exact SKU, match (stock marketplace
-- photography of the same style of garment) — see README §8 and CHANGELOG for full attribution
-- (photographer + Unsplash link required by the Unsplash API guidelines).
MERGE INTO products (id, seller_id, name, description, price, stock_qty, category, image_url, active, created_at) KEY (id) VALUES
  (1, 2, 'Atelier Wool Overcoat', 'Double-faced wool overcoat, hand-finished seams, tonal horn buttons.', 890.00, 12, 'Outerwear', 'https://images.unsplash.com/photo-1539533113208-f6df8cc8b543?q=80&w=900&fit=crop&auto=format', TRUE, CURRENT_TIMESTAMP),
  (2, 2, 'Silk Column Dress',     'Bias-cut silk charmeuse column dress in ink black.', 620.00, 8, 'Dresses', 'https://images.unsplash.com/photo-1651047666890-8eab731ee345?q=80&w=900&fit=crop&auto=format', TRUE, CURRENT_TIMESTAMP),
  (3, 2, 'Leather Structured Tote', 'Vegetable-tanned calfskin tote with brushed brass hardware.', 1150.00, 6, 'Bags', 'https://images.unsplash.com/photo-1624687943971-e86af76d57de?q=80&w=900&fit=crop&auto=format', TRUE, CURRENT_TIMESTAMP),
  (4, 2, 'Cashmere Crewneck', 'Pure Mongolian cashmere crewneck, seamless knit.', 340.00, 20, 'Knitwear', 'https://images.unsplash.com/photo-1604573824419-289a9a10672c?q=80&w=900&fit=crop&auto=format', TRUE, CURRENT_TIMESTAMP),
  (5, 2, 'Court Sneaker — Blanc', 'Minimalist leather court sneaker with vulcanized sole.', 410.00, 25, 'Footwear', 'https://images.unsplash.com/photo-1608379743498-ac08f6d022ba?q=80&w=900&fit=crop&auto=format', TRUE, CURRENT_TIMESTAMP),
  (6, 2, 'Tailored Wool Trouser', 'High-rise straight-leg trouser in Italian wool twill.', 295.00, 18, 'Trousers', 'https://images.unsplash.com/photo-1694447814836-c93ab70f7398?q=80&w=900&fit=crop&auto=format', TRUE, CURRENT_TIMESTAMP),
  (7, 2, 'Signature Aviator Sunglasses', 'Titanium frame, gradient polarized lens.', 265.00, 30, 'Accessories', 'https://images.unsplash.com/photo-1567473810954-507d59716c25?q=80&w=900&fit=crop&auto=format', TRUE, CURRENT_TIMESTAMP),
  (8, 2, 'Performance Runner — Volt', 'Engineered knit upper, responsive foam midsole.', 175.00, 40, 'Footwear', 'https://images.unsplash.com/photo-1746206673199-5b75dcec1018?q=80&w=900&fit=crop&auto=format', TRUE, CURRENT_TIMESTAMP);

ALTER TABLE products ALTER COLUMN id RESTART WITH 9;

MERGE INTO reviews (id, product_id, user_id, rating, comment, created_at) KEY (id) VALUES
  (1, 1, 3, 5, 'Impeccable tailoring, worth every rupee.', CURRENT_TIMESTAMP),
  (2, 5, 3, 4, 'Fits true to size, très chic.', CURRENT_TIMESTAMP);

ALTER TABLE reviews ALTER COLUMN id RESTART WITH 3;
