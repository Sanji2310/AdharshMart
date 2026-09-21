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
-- compare_at_price is set (> price) on 3 listings as the current sale — see the homepage's
-- Sale section and each product card's strike-through price / badge.
MERGE INTO products (id, seller_id, name, description, price, compare_at_price, stock_qty, category, image_url, active, created_at) KEY (id) VALUES
  (1, 2, 'Atelier Wool Overcoat', 'Double-faced wool overcoat, hand-finished seams, tonal horn buttons.', 890.00, 1090.00, 12, 'Outerwear', 'https://images.unsplash.com/photo-1539533113208-f6df8cc8b543?q=80&w=900&fit=crop&auto=format', TRUE, CURRENT_TIMESTAMP),
  (2, 2, 'Silk Column Dress',     'Bias-cut silk charmeuse column dress in ink black.', 620.00, NULL, 8, 'Dresses', 'https://images.unsplash.com/photo-1651047666890-8eab731ee345?q=80&w=900&fit=crop&auto=format', TRUE, CURRENT_TIMESTAMP),
  (3, 2, 'Leather Structured Tote', 'Vegetable-tanned calfskin tote with brushed brass hardware.', 1150.00, NULL, 6, 'Bags', 'https://images.unsplash.com/photo-1624687943971-e86af76d57de?q=80&w=900&fit=crop&auto=format', TRUE, CURRENT_TIMESTAMP),
  (4, 2, 'Cashmere Crewneck', 'Pure Mongolian cashmere crewneck, seamless knit.', 340.00, 420.00, 20, 'Knitwear', 'https://images.unsplash.com/photo-1604573824419-289a9a10672c?q=80&w=900&fit=crop&auto=format', TRUE, CURRENT_TIMESTAMP),
  (5, 2, 'Court Sneaker — Blanc', 'Minimalist leather court sneaker with vulcanized sole.', 410.00, NULL, 25, 'Footwear', 'https://images.unsplash.com/photo-1608379743498-ac08f6d022ba?q=80&w=900&fit=crop&auto=format', TRUE, CURRENT_TIMESTAMP),
  (6, 2, 'Tailored Wool Trouser', 'High-rise straight-leg trouser in Italian wool twill.', 295.00, NULL, 18, 'Trousers', 'https://images.unsplash.com/photo-1694447814836-c93ab70f7398?q=80&w=900&fit=crop&auto=format', TRUE, CURRENT_TIMESTAMP),
  (7, 2, 'Signature Aviator Sunglasses', 'Titanium frame, gradient polarized lens.', 265.00, NULL, 30, 'Accessories', 'https://images.unsplash.com/photo-1567473810954-507d59716c25?q=80&w=900&fit=crop&auto=format', TRUE, CURRENT_TIMESTAMP),
  (8, 2, 'Performance Runner — Volt', 'Engineered knit upper, responsive foam midsole.', 175.00, 225.00, 40, 'Footwear', 'https://images.unsplash.com/photo-1746206673199-5b75dcec1018?q=80&w=900&fit=crop&auto=format', TRUE, CURRENT_TIMESTAMP),
  (9, 2, 'Cropped Denim Jacket', 'Rigid selvedge denim, cropped fit, brushed nickel hardware.', 245.00, NULL, 22, 'Outerwear', 'https://images.unsplash.com/photo-1614699745279-2c61bd9d46b5?q=80&w=900&fit=crop&auto=format', TRUE, CURRENT_TIMESTAMP),
  (10, 2, 'Quilted Puffer Vest', 'Down-fill quilted vest, packable, storm collar.', 180.00, NULL, 16, 'Outerwear', 'https://images.unsplash.com/photo-1636529109797-0749811c4916?q=80&w=900&fit=crop&auto=format', TRUE, CURRENT_TIMESTAMP),
  (11, 2, 'Midi Wrap Dress', 'Fluid crepe wrap dress with self-tie waist, midi length.', 410.00, NULL, 14, 'Dresses', 'https://images.unsplash.com/photo-1592020051126-c240d2aaa98b?q=80&w=900&fit=crop&auto=format', TRUE, CURRENT_TIMESTAMP),
  (12, 2, 'Draped Evening Gown', 'Floor-length draped gown in duchesse satin, fitted bodice.', 780.00, NULL, 5, 'Dresses', 'https://images.unsplash.com/photo-1568252542512-9fe8fe9c87bb?q=80&w=900&fit=crop&auto=format', TRUE, CURRENT_TIMESTAMP),
  (13, 2, 'Canvas Weekender Bag', 'Waxed canvas and leather-trim weekender, brass zip.', 245.00, NULL, 15, 'Bags', 'https://images.unsplash.com/photo-1708622833152-924c6e364138?q=80&w=900&fit=crop&auto=format', TRUE, CURRENT_TIMESTAMP),
  (14, 2, 'Mini Crossbody Bag', 'Structured mini crossbody in smooth calfskin, chain strap.', 195.00, NULL, 28, 'Bags', 'https://images.unsplash.com/photo-1604176424472-17cd740f74e9?q=80&w=900&fit=crop&auto=format', TRUE, CURRENT_TIMESTAMP),
  (15, 2, 'Merino Wool Turtleneck', 'Fine-gauge merino turtleneck, ribbed cuffs and hem.', 220.00, NULL, 24, 'Knitwear', 'https://images.unsplash.com/photo-1574201635302-388dd92a4c3f?q=80&w=900&fit=crop&auto=format', TRUE, CURRENT_TIMESTAMP),
  (16, 2, 'Cable Knit Cardigan', 'Chunky cable-knit cardigan, horn buttons, dropped shoulder.', 310.00, NULL, 17, 'Knitwear', 'https://images.unsplash.com/photo-1683315565563-f72590773805?q=80&w=900&fit=crop&auto=format', TRUE, CURRENT_TIMESTAMP),
  (17, 2, 'Chelsea Boot', 'Polished leather Chelsea boot, elastic gusset, stacked heel.', 365.00, NULL, 20, 'Footwear', 'https://images.unsplash.com/photo-1777987601677-3059be0e1388?q=80&w=900&fit=crop&auto=format', TRUE, CURRENT_TIMESTAMP),
  (18, 2, 'Classic Leather Loafer', 'Hand-stitched penny loafer in burnished calfskin.', 320.00, NULL, 22, 'Footwear', 'https://images.unsplash.com/photo-1777987601447-266e128de448?q=80&w=900&fit=crop&auto=format', TRUE, CURRENT_TIMESTAMP),
  (19, 2, 'Straight Leg Denim', 'Mid-rise straight-leg jean in rigid Japanese denim.', 165.00, NULL, 35, 'Trousers', 'https://images.unsplash.com/photo-1777113310267-9e838b284732?q=80&w=900&fit=crop&auto=format', TRUE, CURRENT_TIMESTAMP),
  (20, 2, 'Pleated Wide-Leg Trouser', 'Double-pleated wide-leg trouser in fluid viscose twill.', 210.00, NULL, 19, 'Trousers', 'https://images.unsplash.com/photo-1789110520665-f07353f0afbe?q=80&w=900&fit=crop&auto=format', TRUE, CURRENT_TIMESTAMP),
  (21, 2, 'Reversible Leather Belt', 'Full-grain reversible belt, brushed brass buckle.', 95.00, NULL, 45, 'Accessories', 'https://images.unsplash.com/photo-1711443982852-b3df5c563448?q=80&w=900&fit=crop&auto=format', TRUE, CURRENT_TIMESTAMP),
  (22, 2, 'Silk Twill Scarf', 'Hand-rolled hem silk twill scarf, archive print.', 130.00, NULL, 33, 'Accessories', 'https://images.unsplash.com/photo-1551028442-ee84b4d3a50a?q=80&w=900&fit=crop&auto=format', TRUE, CURRENT_TIMESTAMP),
  (23, 2, 'Tailored Blazer', 'Single-breasted wool blazer, half-canvas construction.', 590.00, NULL, 12, 'Formalwear', 'https://images.unsplash.com/photo-1617127365659-c47fa864d8bc?q=80&w=900&fit=crop&auto=format', TRUE, CURRENT_TIMESTAMP),
  (24, 2, 'Three-Piece Suit', 'Notch-lapel three-piece suit in Super 120s wool.', 980.00, NULL, 6, 'Formalwear', 'https://images.unsplash.com/photo-1617137984095-74e4e5e3613f?q=80&w=900&fit=crop&auto=format', TRUE, CURRENT_TIMESTAMP),
  (25, 2, 'Cotton Oxford Shirt', 'Crisp cotton Oxford shirt, mother-of-pearl buttons.', 120.00, NULL, 40, 'Shirts', 'https://images.unsplash.com/photo-1612541122840-bf7071c968a2?q=80&w=900&fit=crop&auto=format', TRUE, CURRENT_TIMESTAMP);

ALTER TABLE products ALTER COLUMN id RESTART WITH 26;

MERGE INTO reviews (id, product_id, user_id, rating, comment, created_at) KEY (id) VALUES
  (1, 1, 3, 5, 'Impeccable tailoring, worth every rupee.', CURRENT_TIMESTAMP),
  (2, 5, 3, 4, 'Fits true to size, très chic.', CURRENT_TIMESTAMP);

ALTER TABLE reviews ALTER COLUMN id RESTART WITH 3;
