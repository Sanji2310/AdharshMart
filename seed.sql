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

-- Product imagery: real Unsplash photos, each individually verified for a genuinely isolated
-- white/neutral background with no dominant person in frame — see photo-credits.jsp. Where no
-- such photo existed for the original garment concept (coats, gowns, patterned trousers), the
-- product itself was changed to one with real clean product photography available.
-- compare_at_price is set (> price) on 3 listings as the current sale — see the homepage's
-- Sale section and each product card's strike-through price / badge.
MERGE INTO products (id, seller_id, name, description, price, compare_at_price, stock_qty, category, image_url, active, created_at) KEY (id) VALUES
  (1, 2, 'Linen Blazer', 'Relaxed linen blazer, unstructured shoulder, soft drape.', 8500.00, 11000.00, 12, 'Outerwear', 'https://images.unsplash.com/photo-1740710370552-a49b5b01f80a?q=80&w=900&fit=crop&auto=format', TRUE, CURRENT_TIMESTAMP),
  (2, 2, 'White Tuxedo Jacket', 'Double-breasted tuxedo jacket in midnight wool, satin lapel.', 5200.00, NULL, 8, 'Formalwear', 'https://images.unsplash.com/photo-1789163278539-72c8c7026a3f?q=80&w=900&fit=crop&auto=format', TRUE, CURRENT_TIMESTAMP),
  (3, 2, 'Leather Structured Tote', 'Vegetable-tanned calfskin tote with brushed brass hardware.', 9800.00, NULL, 6, 'Bags', 'https://images.unsplash.com/photo-1691480150204-66dd1eb77391?q=80&w=900&fit=crop&auto=format', TRUE, CURRENT_TIMESTAMP),
  (4, 2, 'Cashmere Crewneck', 'Pure Mongolian cashmere crewneck, seamless knit.', 3200.00, 4200.00, 20, 'Knitwear', 'https://images.unsplash.com/photo-1620799139507-2a76f79a2f4d?q=80&w=900&fit=crop&auto=format', TRUE, CURRENT_TIMESTAMP),
  (5, 2, 'Court Sneaker — Blanc', 'Minimalist leather court sneaker with vulcanized sole.', 3800.00, NULL, 25, 'Footwear', 'https://images.unsplash.com/photo-1625860191460-10a66c7384fb?q=80&w=900&fit=crop&auto=format', TRUE, CURRENT_TIMESTAMP),
  (6, 2, 'Raw Selvedge Denim', 'Rigid raw selvedge jean, straight fit, indigo warp.', 2600.00, NULL, 18, 'Trousers', 'https://images.unsplash.com/photo-1718252540558-7b383b52642e?q=80&w=900&fit=crop&auto=format', TRUE, CURRENT_TIMESTAMP),
  (7, 2, 'Signature Aviator Sunglasses', 'Titanium frame, gradient polarized lens.', 2900.00, NULL, 30, 'Accessories', 'https://images.unsplash.com/photo-1710407625705-fe00b2ecf9e7?q=80&w=900&fit=crop&auto=format', TRUE, CURRENT_TIMESTAMP),
  (8, 2, 'Performance Runner — Volt', 'Engineered knit upper, responsive foam midsole.', 2200.00, 2800.00, 40, 'Footwear', 'https://images.unsplash.com/photo-1786379582231-f4a593cacf2d?q=80&w=900&fit=crop&auto=format', TRUE, CURRENT_TIMESTAMP),
  (9, 2, 'Cropped Denim Jacket', 'Rigid selvedge denim, cropped fit, brushed nickel hardware.', 2600.00, NULL, 22, 'Outerwear', 'https://images.unsplash.com/photo-1708523842501-1619478cea1f?q=80&w=900&fit=crop&auto=format', TRUE, CURRENT_TIMESTAMP),
  (10, 2, 'Relaxed Linen Overshirt', 'Lightweight linen overshirt, relaxed fit, single chest pocket.', 3400.00, NULL, 16, 'Outerwear', 'https://images.unsplash.com/photo-1740710748146-a15d840d6f40?q=80&w=900&fit=crop&auto=format', TRUE, CURRENT_TIMESTAMP),
  (11, 2, 'Beaded Statement Necklace', 'Hand-strung beaded statement necklace, mixed metallic finish.', 3800.00, NULL, 14, 'Accessories', 'https://images.unsplash.com/photo-1718312267215-58bbba315a15?q=80&w=900&fit=crop&auto=format', TRUE, CURRENT_TIMESTAMP),
  (12, 2, 'Leather Bifold Wallet', 'Full-grain leather bifold wallet, six card slots, coin pocket.', 12500.00, NULL, 5, 'Accessories', 'https://images.unsplash.com/photo-1626151453023-f56216bfc923?q=80&w=900&fit=crop&auto=format', TRUE, CURRENT_TIMESTAMP),
  (13, 2, 'Canvas Travel Pouch', 'Waxed canvas travel pouch, leather trim, brass zip.', 4800.00, NULL, 15, 'Bags', 'https://images.unsplash.com/photo-1615485737442-7d6ab9f64db9?q=80&w=900&fit=crop&auto=format', TRUE, CURRENT_TIMESTAMP),
  (14, 2, 'Mini Crossbody Bag', 'Structured mini crossbody in smooth calfskin, chain strap.', 4200.00, NULL, 28, 'Bags', 'https://images.unsplash.com/photo-1691480250099-a63081ecfcb8?q=80&w=900&fit=crop&auto=format', TRUE, CURRENT_TIMESTAMP),
  (15, 2, 'Merino Wool Turtleneck', 'Fine-gauge merino turtleneck, ribbed cuffs and hem.', 2600.00, NULL, 24, 'Knitwear', 'https://images.unsplash.com/photo-1621198059871-0d5f9b449233?q=80&w=900&fit=crop&auto=format', TRUE, CURRENT_TIMESTAMP),
  (16, 2, 'Cable Knit Cardigan', 'Chunky cable-knit cardigan, horn buttons, dropped shoulder.', 3600.00, NULL, 17, 'Knitwear', 'https://images.unsplash.com/photo-1536992266094-82847e1fd431?q=80&w=900&fit=crop&auto=format', TRUE, CURRENT_TIMESTAMP),
  (17, 2, 'Leather Lace-Up Boot', 'Rugged leather lace-up boot, waxed laces, lug sole.', 4200.00, NULL, 20, 'Footwear', 'https://images.unsplash.com/photo-1550998358-08b4f83dc345?q=80&w=900&fit=crop&auto=format', TRUE, CURRENT_TIMESTAMP),
  (18, 2, 'Pointed Leather Heel', 'Hand-finished pointed-toe heel in burnished calfskin.', 3600.00, NULL, 22, 'Footwear', 'https://images.unsplash.com/photo-1789110519431-0a9bf0af5074?q=80&w=900&fit=crop&auto=format', TRUE, CURRENT_TIMESTAMP),
  (19, 2, 'Straight Leg Denim', 'Mid-rise straight-leg jean in rigid Japanese denim.', 2000.00, NULL, 35, 'Trousers', 'https://images.unsplash.com/photo-1637069585336-827b298fe84a?q=80&w=900&fit=crop&auto=format', TRUE, CURRENT_TIMESTAMP),
  (20, 2, 'Cropped Wide-Leg Jean', 'Cropped wide-leg jean, high-rise, raw hem.', 2400.00, NULL, 19, 'Trousers', 'https://images.unsplash.com/photo-1718252540511-e958742e4165?q=80&w=900&fit=crop&auto=format', TRUE, CURRENT_TIMESTAMP),
  (21, 2, 'Reversible Leather Belt', 'Full-grain reversible belt, brushed brass buckle.', 1600.00, NULL, 45, 'Accessories', 'https://images.unsplash.com/photo-1752386341161-de2b02ea1f50?q=80&w=900&fit=crop&auto=format', TRUE, CURRENT_TIMESTAMP),
  (22, 2, 'Silk Twill Scarf', 'Hand-rolled hem silk twill scarf, archive print.', 1900.00, NULL, 33, 'Accessories', 'https://images.unsplash.com/photo-1643312892626-80632ef6d7dc?q=80&w=900&fit=crop&auto=format', TRUE, CURRENT_TIMESTAMP),
  (23, 2, 'Tailored Blazer', 'Single-breasted wool blazer, half-canvas construction.', 6200.00, NULL, 12, 'Formalwear', 'https://images.unsplash.com/photo-1740650874524-4f57a78e5878?q=80&w=900&fit=crop&auto=format', TRUE, CURRENT_TIMESTAMP),
  (24, 2, 'Vintage-Inspired Leather Watch', 'Automatic movement watch, brown leather strap, sunburst dial.', 15500.00, NULL, 6, 'Accessories', 'https://images.unsplash.com/photo-1758887952896-8491d393afe2?q=80&w=900&fit=crop&auto=format', TRUE, CURRENT_TIMESTAMP),
  (25, 2, 'Cotton Oxford Shirt', 'Crisp cotton Oxford shirt, mother-of-pearl buttons.', 1400.00, NULL, 40, 'Shirts', 'https://images.unsplash.com/photo-1776838103951-993ff1ffc916?q=80&w=900&fit=crop&auto=format', TRUE, CURRENT_TIMESTAMP);

ALTER TABLE products ALTER COLUMN id RESTART WITH 26;

-- Multi-image gallery removed per user feedback ("just one is enough, make it clean") — the
-- product_images table stays in the schema (see ProductImageDAO) but is seeded empty, so every
-- product shows exactly one image, everywhere, on card and detail page alike.

-- Size and color options shown as selectable boxes/swatches on the product detail page.
-- Footwear gets EU shoe sizes, trousers/belts get waist sizes, other apparel gets S-XXL;
-- bags and a few accessories (sunglasses, scarf) carry no size rows at all, matching how
-- real e-commerce only shows a size selector where sizing is actually meaningful.
MERGE INTO product_sizes (id, product_id, label, sort_order) KEY (id) VALUES
  (1, 1, 'S', 1), (2, 1, 'M', 2), (3, 1, 'L', 3), (4, 1, 'XL', 4),
  (5, 2, 'XS', 1), (6, 2, 'S', 2), (7, 2, 'M', 3), (8, 2, 'L', 4),
  (9, 4, 'XS', 1), (10, 4, 'S', 2), (11, 4, 'M', 3), (12, 4, 'L', 4), (13, 4, 'XL', 5),
  (14, 5, '39', 1), (15, 5, '40', 2), (16, 5, '41', 3), (17, 5, '42', 4), (18, 5, '43', 5), (19, 5, '44', 6), (20, 5, '45', 7),
  (21, 6, '28', 1), (22, 6, '30', 2), (23, 6, '32', 3), (24, 6, '34', 4), (25, 6, '36', 5), (26, 6, '38', 6),
  (27, 8, '39', 1), (28, 8, '40', 2), (29, 8, '41', 3), (30, 8, '42', 4), (31, 8, '43', 5), (32, 8, '44', 6), (33, 8, '45', 7),
  (34, 9, 'XS', 1), (35, 9, 'S', 2), (36, 9, 'M', 3), (37, 9, 'L', 4), (38, 9, 'XL', 5),
  (51, 15, 'XS', 1), (52, 15, 'S', 2), (53, 15, 'M', 3), (54, 15, 'L', 4), (55, 15, 'XL', 5),
  (56, 16, 'S', 1), (57, 16, 'M', 2), (58, 16, 'L', 3), (59, 16, 'XL', 4),
  (60, 17, '39', 1), (61, 17, '40', 2), (62, 17, '41', 3), (63, 17, '42', 4), (64, 17, '43', 5), (65, 17, '44', 6), (66, 17, '45', 7),
  (67, 18, '39', 1), (68, 18, '40', 2), (69, 18, '41', 3), (70, 18, '42', 4), (71, 18, '43', 5), (72, 18, '44', 6), (73, 18, '45', 7),
  (74, 19, '28', 1), (75, 19, '30', 2), (76, 19, '32', 3), (77, 19, '34', 4), (78, 19, '36', 5), (79, 19, '38', 6),
  (80, 20, 'XS', 1), (81, 20, 'S', 2), (82, 20, 'M', 3), (83, 20, 'L', 4), (84, 20, 'XL', 5),
  (85, 21, '32', 1), (86, 21, '34', 2), (87, 21, '36', 3), (88, 21, '38', 4), (89, 21, '40', 5),
  (100, 25, 'S', 1), (101, 25, 'M', 2), (102, 25, 'L', 3), (103, 25, 'XL', 4), (104, 25, 'XXL', 5),
  (105, 10, 'S', 1), (106, 10, 'M', 2), (107, 10, 'L', 3), (108, 10, 'XL', 4),
  (113, 23, 'S', 1), (114, 23, 'M', 2), (115, 23, 'L', 3), (116, 23, 'XL', 4), (117, 23, 'XXL', 5);

ALTER TABLE product_sizes ALTER COLUMN id RESTART WITH 124;

MERGE INTO product_colors (id, product_id, name, hex_code, sort_order) KEY (id) VALUES
  (1, 1, 'Camel', '#C19A6B', 1), (2, 1, 'Charcoal', '#36454F', 2), (3, 1, 'Black', '#1C1C1A', 3),
  (4, 2, 'Ink Black', '#0B0B0C', 1), (5, 2, 'Ivory', '#F1EDE4', 2),
  (6, 3, 'Cognac', '#8B4513', 1), (7, 3, 'Black', '#1C1C1A', 2), (8, 3, 'Taupe', '#8B7D6B', 3),
  (9, 4, 'Oatmeal', '#D8CBB8', 1), (10, 4, 'Charcoal', '#36454F', 2), (11, 4, 'Navy', '#1B2A4A', 3), (12, 4, 'Burgundy', '#6D2E3A', 4),
  (13, 5, 'Blanc', '#F5F5F0', 1), (14, 5, 'Black', '#1C1C1A', 2),
  (15, 6, 'Charcoal', '#36454F', 1), (16, 6, 'Navy', '#1B2A4A', 2), (17, 6, 'Black', '#1C1C1A', 3),
  (18, 7, 'Gunmetal', '#4B4B4D', 1), (19, 7, 'Gold', '#C9A227', 2), (20, 7, 'Tortoise', '#6B4226', 3),
  (21, 8, 'Volt', '#C6F135', 1), (22, 8, 'Black', '#1C1C1A', 2), (23, 8, 'White', '#F5F5F0', 3),
  (24, 9, 'Raw Indigo', '#2C3E60', 1), (25, 9, 'Black', '#1C1C1A', 2),
  (26, 10, 'Black', '#1C1C1A', 1), (27, 10, 'Olive', '#5B5A3A', 2), (28, 10, 'Navy', '#1B2A4A', 3),
  (29, 11, 'Terracotta', '#B4592B', 1), (30, 11, 'Black', '#1C1C1A', 2), (31, 11, 'Emerald', '#2F5D50', 3),
  (32, 12, 'Black', '#0B0B0C', 1), (33, 12, 'Deep Red', '#7A1F2B', 2), (34, 12, 'Midnight Blue', '#1B2140', 3),
  (35, 13, 'Waxed Olive', '#5B5A3A', 1), (36, 13, 'Tan', '#C19A6B', 2),
  (37, 14, 'Black', '#1C1C1A', 1), (38, 14, 'Cognac', '#8B4513', 2), (39, 14, 'Cream', '#EFE6D8', 3),
  (40, 15, 'Charcoal', '#36454F', 1), (41, 15, 'Camel', '#C19A6B', 2), (42, 15, 'Black', '#1C1C1A', 3), (43, 15, 'Forest', '#2F4538', 4),
  (44, 16, 'Oatmeal', '#D8CBB8', 1), (45, 16, 'Charcoal', '#36454F', 2),
  (46, 17, 'Black', '#1C1C1A', 1), (47, 17, 'Brown', '#5C3A21', 2),
  (48, 18, 'Burgundy', '#6D2E3A', 1), (49, 18, 'Black', '#1C1C1A', 2), (50, 18, 'Tan', '#C19A6B', 3),
  (51, 19, 'Indigo', '#2C3E60', 1), (52, 19, 'Black', '#1C1C1A', 2),
  (53, 20, 'Charcoal', '#36454F', 1), (54, 20, 'Sand', '#C9B896', 2),
  (55, 21, 'Black', '#1C1C1A', 1), (56, 21, 'Cognac', '#8B4513', 2),
  (57, 22, 'Gold Archive', '#C9A227', 1), (58, 22, 'Crimson Archive', '#7A1F2B', 2),
  (59, 23, 'Navy', '#1B2A4A', 1), (60, 23, 'Charcoal', '#36454F', 2), (61, 23, 'Black', '#1C1C1A', 3),
  (62, 24, 'Charcoal', '#36454F', 1), (63, 24, 'Navy', '#1B2A4A', 2),
  (64, 25, 'White', '#F5F5F0', 1), (65, 25, 'Sky Blue', '#A9C4DE', 2), (66, 25, 'Ecru', '#EFE6D8', 3);

ALTER TABLE product_colors ALTER COLUMN id RESTART WITH 67;

MERGE INTO reviews (id, product_id, user_id, rating, comment, created_at) KEY (id) VALUES
  (1, 1, 3, 5, 'Impeccable tailoring, worth every rupee.', CURRENT_TIMESTAMP),
  (2, 5, 3, 4, 'Fits true to size, très chic.', CURRENT_TIMESTAMP);

ALTER TABLE reviews ALTER COLUMN id RESTART WITH 3;
