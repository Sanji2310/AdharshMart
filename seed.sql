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
  (1, 2, 'Atelier Wool Overcoat', 'Double-faced wool overcoat, hand-finished seams, tonal horn buttons.', 68000.00, 84000.00, 12, 'Outerwear', 'https://images.unsplash.com/photo-1539533113208-f6df8cc8b543?q=80&w=900&fit=crop&auto=format', TRUE, CURRENT_TIMESTAMP),
  (2, 2, 'Silk Column Dress',     'Bias-cut silk charmeuse column dress in ink black.', 42500.00, NULL, 8, 'Dresses', 'https://images.unsplash.com/photo-1651047666890-8eab731ee345?q=80&w=900&fit=crop&auto=format', TRUE, CURRENT_TIMESTAMP),
  (3, 2, 'Leather Structured Tote', 'Vegetable-tanned calfskin tote with brushed brass hardware.', 78000.00, NULL, 6, 'Bags', 'https://images.unsplash.com/photo-1624687943971-e86af76d57de?q=80&w=900&fit=crop&auto=format', TRUE, CURRENT_TIMESTAMP),
  (4, 2, 'Cashmere Crewneck', 'Pure Mongolian cashmere crewneck, seamless knit.', 24500.00, 32000.00, 20, 'Knitwear', 'https://images.unsplash.com/photo-1604573824419-289a9a10672c?q=80&w=900&fit=crop&auto=format', TRUE, CURRENT_TIMESTAMP),
  (5, 2, 'Court Sneaker — Blanc', 'Minimalist leather court sneaker with vulcanized sole.', 28500.00, NULL, 25, 'Footwear', 'https://images.unsplash.com/photo-1608379743498-ac08f6d022ba?q=80&w=900&fit=crop&auto=format', TRUE, CURRENT_TIMESTAMP),
  (6, 2, 'Tailored Wool Trouser', 'High-rise straight-leg trouser in Italian wool twill.', 19500.00, NULL, 18, 'Trousers', 'https://images.unsplash.com/photo-1694447814836-c93ab70f7398?q=80&w=900&fit=crop&auto=format', TRUE, CURRENT_TIMESTAMP),
  (7, 2, 'Signature Aviator Sunglasses', 'Titanium frame, gradient polarized lens.', 22000.00, NULL, 30, 'Accessories', 'https://images.unsplash.com/photo-1567473810954-507d59716c25?q=80&w=900&fit=crop&auto=format', TRUE, CURRENT_TIMESTAMP),
  (8, 2, 'Performance Runner — Volt', 'Engineered knit upper, responsive foam midsole.', 14500.00, 18500.00, 40, 'Footwear', 'https://images.unsplash.com/photo-1746206673199-5b75dcec1018?q=80&w=900&fit=crop&auto=format', TRUE, CURRENT_TIMESTAMP),
  (9, 2, 'Cropped Denim Jacket', 'Rigid selvedge denim, cropped fit, brushed nickel hardware.', 17500.00, NULL, 22, 'Outerwear', 'https://images.unsplash.com/photo-1614699745279-2c61bd9d46b5?q=80&w=900&fit=crop&auto=format', TRUE, CURRENT_TIMESTAMP),
  (10, 2, 'Quilted Puffer Vest', 'Down-fill quilted vest, packable, storm collar.', 21000.00, NULL, 16, 'Outerwear', 'https://images.unsplash.com/photo-1636529109797-0749811c4916?q=80&w=900&fit=crop&auto=format', TRUE, CURRENT_TIMESTAMP),
  (11, 2, 'Midi Wrap Dress', 'Fluid crepe wrap dress with self-tie waist, midi length.', 29500.00, NULL, 14, 'Dresses', 'https://images.unsplash.com/photo-1592020051126-c240d2aaa98b?q=80&w=900&fit=crop&auto=format', TRUE, CURRENT_TIMESTAMP),
  (12, 2, 'Draped Evening Gown', 'Floor-length draped gown in duchesse satin, fitted bodice.', 95000.00, NULL, 5, 'Dresses', 'https://images.unsplash.com/photo-1568252542512-9fe8fe9c87bb?q=80&w=900&fit=crop&auto=format', TRUE, CURRENT_TIMESTAMP),
  (13, 2, 'Canvas Weekender Bag', 'Waxed canvas and leather-trim weekender, brass zip.', 38000.00, NULL, 15, 'Bags', 'https://images.unsplash.com/photo-1708622833152-924c6e364138?q=80&w=900&fit=crop&auto=format', TRUE, CURRENT_TIMESTAMP),
  (14, 2, 'Mini Crossbody Bag', 'Structured mini crossbody in smooth calfskin, chain strap.', 32500.00, NULL, 28, 'Bags', 'https://images.unsplash.com/photo-1604176424472-17cd740f74e9?q=80&w=900&fit=crop&auto=format', TRUE, CURRENT_TIMESTAMP),
  (15, 2, 'Merino Wool Turtleneck', 'Fine-gauge merino turtleneck, ribbed cuffs and hem.', 18500.00, NULL, 24, 'Knitwear', 'https://images.unsplash.com/photo-1574201635302-388dd92a4c3f?q=80&w=900&fit=crop&auto=format', TRUE, CURRENT_TIMESTAMP),
  (16, 2, 'Cable Knit Cardigan', 'Chunky cable-knit cardigan, horn buttons, dropped shoulder.', 26500.00, NULL, 17, 'Knitwear', 'https://images.unsplash.com/photo-1683315565563-f72590773805?q=80&w=900&fit=crop&auto=format', TRUE, CURRENT_TIMESTAMP),
  (17, 2, 'Chelsea Boot', 'Polished leather Chelsea boot, elastic gusset, stacked heel.', 32000.00, NULL, 20, 'Footwear', 'https://images.unsplash.com/photo-1777987601677-3059be0e1388?q=80&w=900&fit=crop&auto=format', TRUE, CURRENT_TIMESTAMP),
  (18, 2, 'Classic Leather Loafer', 'Hand-stitched penny loafer in burnished calfskin.', 27500.00, NULL, 22, 'Footwear', 'https://images.unsplash.com/photo-1777987601447-266e128de448?q=80&w=900&fit=crop&auto=format', TRUE, CURRENT_TIMESTAMP),
  (19, 2, 'Straight Leg Denim', 'Mid-rise straight-leg jean in rigid Japanese denim.', 13500.00, NULL, 35, 'Trousers', 'https://images.unsplash.com/photo-1777113310267-9e838b284732?q=80&w=900&fit=crop&auto=format', TRUE, CURRENT_TIMESTAMP),
  (20, 2, 'Pleated Wide-Leg Trouser', 'Double-pleated wide-leg trouser in fluid viscose twill.', 16500.00, NULL, 19, 'Trousers', 'https://images.unsplash.com/photo-1789110520665-f07353f0afbe?q=80&w=900&fit=crop&auto=format', TRUE, CURRENT_TIMESTAMP),
  (21, 2, 'Reversible Leather Belt', 'Full-grain reversible belt, brushed brass buckle.', 11500.00, NULL, 45, 'Accessories', 'https://images.unsplash.com/photo-1711443982852-b3df5c563448?q=80&w=900&fit=crop&auto=format', TRUE, CURRENT_TIMESTAMP),
  (22, 2, 'Silk Twill Scarf', 'Hand-rolled hem silk twill scarf, archive print.', 14000.00, NULL, 33, 'Accessories', 'https://images.unsplash.com/photo-1551028442-ee84b4d3a50a?q=80&w=900&fit=crop&auto=format', TRUE, CURRENT_TIMESTAMP),
  (23, 2, 'Tailored Blazer', 'Single-breasted wool blazer, half-canvas construction.', 48000.00, NULL, 12, 'Formalwear', 'https://images.unsplash.com/photo-1617127365659-c47fa864d8bc?q=80&w=900&fit=crop&auto=format', TRUE, CURRENT_TIMESTAMP),
  (24, 2, 'Three-Piece Suit', 'Notch-lapel three-piece suit in Super 120s wool.', 125000.00, NULL, 6, 'Formalwear', 'https://images.unsplash.com/photo-1617137984095-74e4e5e3613f?q=80&w=900&fit=crop&auto=format', TRUE, CURRENT_TIMESTAMP),
  (25, 2, 'Cotton Oxford Shirt', 'Crisp cotton Oxford shirt, mother-of-pearl buttons.', 9500.00, NULL, 40, 'Shirts', 'https://images.unsplash.com/photo-1612541122840-bf7071c968a2?q=80&w=900&fit=crop&auto=format', TRUE, CURRENT_TIMESTAMP);

ALTER TABLE products ALTER COLUMN id RESTART WITH 26;

-- Product detail gallery: 2-3 additional real Unsplash photos per product, each individually
-- searched to match the product's category/style (texture, worn, and styling angles) — same
-- sourcing standard as the primary image_url above. products.image_url remains the cover shot
-- used everywhere a single thumbnail is needed (grid cards, cart lines); these rows are shown
-- only on the product detail page gallery, in sort_order after the cover shot.
MERGE INTO product_images (id, product_id, image_url, sort_order) KEY (id) VALUES
  (1, 1, 'https://images.unsplash.com/photo-1619603364904-c0498317e145?q=80&w=900&fit=crop&auto=format', 1),
  (2, 1, 'https://images.unsplash.com/photo-1669575903350-9a349b411810?q=80&w=900&fit=crop&auto=format', 2),
  (3, 1, 'https://images.unsplash.com/photo-1619603364937-8d7af41ef206?q=80&w=900&fit=crop&auto=format', 3),
  (4, 2, 'https://images.unsplash.com/photo-1652445830470-9852667150b0?q=80&w=900&fit=crop&auto=format', 1),
  (5, 2, 'https://images.unsplash.com/photo-1744502671648-7cd2358a193f?q=80&w=900&fit=crop&auto=format', 2),
  (6, 2, 'https://images.unsplash.com/photo-1788961138963-2874477b72cd?q=80&w=900&fit=crop&auto=format', 3),
  (7, 3, 'https://images.unsplash.com/photo-1654707636750-ab67a11b21b7?q=80&w=900&fit=crop&auto=format', 1),
  (8, 3, 'https://images.unsplash.com/photo-1760624089496-01ae68a92d58?q=80&w=900&fit=crop&auto=format', 2),
  (9, 3, 'https://images.unsplash.com/photo-1760624294535-40dfdc84a48f?q=80&w=900&fit=crop&auto=format', 3),
  (10, 4, 'https://images.unsplash.com/photo-1636146049394-0924c2b66104?q=80&w=900&fit=crop&auto=format', 1),
  (11, 4, 'https://images.unsplash.com/photo-1631541909061-71e349d1f203?q=80&w=900&fit=crop&auto=format', 2),
  (12, 4, 'https://images.unsplash.com/photo-1677847208228-fe3c52feb441?q=80&w=900&fit=crop&auto=format', 3),
  (13, 5, 'https://images.unsplash.com/photo-1600269452121-4f2416e55c28?q=80&w=900&fit=crop&auto=format', 1),
  (14, 5, 'https://images.unsplash.com/photo-1512374382149-233c42b6a83b?q=80&w=900&fit=crop&auto=format', 2),
  (15, 5, 'https://images.unsplash.com/photo-1597350584914-55bb62285896?q=80&w=900&fit=crop&auto=format', 3),
  (16, 6, 'https://images.unsplash.com/photo-1517445312882-bc9910d016b7?q=80&w=900&fit=crop&auto=format', 1),
  (17, 6, 'https://images.unsplash.com/photo-1601762845238-ddbe21be9b32?q=80&w=900&fit=crop&auto=format', 2),
  (18, 6, 'https://images.unsplash.com/photo-1649566650740-cb0a625e1b40?q=80&w=900&fit=crop&auto=format', 3),
  (19, 7, 'https://images.unsplash.com/photo-1599705709640-9f9eb5964485?q=80&w=900&fit=crop&auto=format', 1),
  (20, 7, 'https://images.unsplash.com/photo-1759227922040-0ca4d3cbe42e?q=80&w=900&fit=crop&auto=format', 2),
  (21, 7, 'https://images.unsplash.com/photo-1747731141445-7656d7467969?q=80&w=900&fit=crop&auto=format', 3),
  (22, 8, 'https://images.unsplash.com/photo-1560769629-975ec94e6a86?q=80&w=900&fit=crop&auto=format', 1),
  (23, 8, 'https://images.unsplash.com/photo-1597892657493-6847b9640bac?q=80&w=900&fit=crop&auto=format', 2),
  (24, 8, 'https://images.unsplash.com/photo-1562183241-b937e95585b6?q=80&w=900&fit=crop&auto=format', 3),
  (25, 9, 'https://images.unsplash.com/photo-1611312449408-fcece27cdbb7?q=80&w=900&fit=crop&auto=format', 1),
  (26, 9, 'https://images.unsplash.com/photo-1555583743-991174c11425?q=80&w=900&fit=crop&auto=format', 2),
  (27, 10, 'https://images.unsplash.com/photo-1772319713406-6b0ec4772086?q=80&w=900&fit=crop&auto=format', 1),
  (28, 10, 'https://images.unsplash.com/photo-1780969393713-6742133843b5?q=80&w=900&fit=crop&auto=format', 2),
  (29, 11, 'https://images.unsplash.com/photo-1612722432474-b971cdcea546?q=80&w=900&fit=crop&auto=format', 1),
  (30, 11, 'https://images.unsplash.com/photo-1532579853048-ec5f8f15f88d?q=80&w=900&fit=crop&auto=format', 2),
  (31, 11, 'https://images.unsplash.com/photo-1759992878340-665575a0832e?q=80&w=900&fit=crop&auto=format', 3),
  (32, 12, 'https://images.unsplash.com/photo-1762430790606-bf626757a00b?q=80&w=900&fit=crop&auto=format', 1),
  (33, 12, 'https://images.unsplash.com/photo-1623580674393-edf6eb7090f8?q=80&w=900&fit=crop&auto=format', 2),
  (34, 13, 'https://images.unsplash.com/photo-1531938716357-224c16b5ace3?q=80&w=900&fit=crop&auto=format', 1),
  (35, 13, 'https://images.unsplash.com/photo-1692506530242-c12d6c3ae2e2?q=80&w=900&fit=crop&auto=format', 2),
  (36, 13, 'https://images.unsplash.com/photo-1535120927584-0230f40fc1e2?q=80&w=900&fit=crop&auto=format', 3),
  (37, 14, 'https://images.unsplash.com/photo-1583623733237-4d5764a9dc82?q=80&w=900&fit=crop&auto=format', 1),
  (38, 14, 'https://images.unsplash.com/photo-1600857125164-499a823272b4?q=80&w=900&fit=crop&auto=format', 2),
  (39, 14, 'https://images.unsplash.com/photo-1626931291835-f1d59553aa2e?q=80&w=900&fit=crop&auto=format', 3),
  (40, 15, 'https://images.unsplash.com/photo-1610901157620-340856d0a50f?q=80&w=900&fit=crop&auto=format', 1),
  (41, 15, 'https://images.unsplash.com/photo-1715176531842-7ffda4acdfa9?q=80&w=900&fit=crop&auto=format', 2),
  (42, 16, 'https://images.unsplash.com/photo-1758981400298-78cd18eb6793?q=80&w=900&fit=crop&auto=format', 1),
  (43, 16, 'https://images.unsplash.com/photo-1629580628926-9fbdffb346b3?q=80&w=900&fit=crop&auto=format', 2),
  (44, 16, 'https://images.unsplash.com/photo-1579206464424-7e43a81cadc1?q=80&w=900&fit=crop&auto=format', 3),
  (45, 17, 'https://images.unsplash.com/photo-1788478963160-1c38139e2932?q=80&w=900&fit=crop&auto=format', 1),
  (46, 17, 'https://images.unsplash.com/photo-1788478963145-4b90e3010698?q=80&w=900&fit=crop&auto=format', 2),
  (47, 17, 'https://images.unsplash.com/photo-1777987601423-f350ac29b3e9?q=80&w=900&fit=crop&auto=format', 3),
  (48, 18, 'https://images.unsplash.com/photo-1556004583-d2aaffbba592?q=80&w=900&fit=crop&auto=format', 1),
  (49, 18, 'https://images.unsplash.com/photo-1760616172899-0681b97a2de3?q=80&w=900&fit=crop&auto=format', 2),
  (50, 18, 'https://images.unsplash.com/photo-1678784973551-f38208de2529?q=80&w=900&fit=crop&auto=format', 3),
  (51, 19, 'https://images.unsplash.com/photo-1598554747436-c9293d6a588f?q=80&w=900&fit=crop&auto=format', 1),
  (52, 19, 'https://images.unsplash.com/photo-1754555009601-498e9873197e?q=80&w=900&fit=crop&auto=format', 2),
  (53, 19, 'https://images.unsplash.com/photo-1629045246540-16a761db8b35?q=80&w=900&fit=crop&auto=format', 3),
  (54, 20, 'https://images.unsplash.com/photo-1687825515654-23620796760c?q=80&w=900&fit=crop&auto=format', 1),
  (55, 20, 'https://images.unsplash.com/photo-1789110520143-9f54f069b43f?q=80&w=900&fit=crop&auto=format', 2),
  (56, 20, 'https://images.unsplash.com/photo-1762343291713-0d7f83e6c2e9?q=80&w=900&fit=crop&auto=format', 3),
  (57, 21, 'https://images.unsplash.com/photo-1637868796504-32f45a96d5a0?q=80&w=900&fit=crop&auto=format', 1),
  (58, 21, 'https://images.unsplash.com/photo-1734383524180-3c6f9b21e8e3?q=80&w=900&fit=crop&auto=format', 2),
  (59, 22, 'https://images.unsplash.com/photo-1677478863154-55ecce8c7536?q=80&w=900&fit=crop&auto=format', 1),
  (60, 22, 'https://images.unsplash.com/photo-1707978932202-751b08324daf?q=80&w=900&fit=crop&auto=format', 2),
  (61, 22, 'https://images.unsplash.com/photo-1689193502879-362660fad4a8?q=80&w=900&fit=crop&auto=format', 3),
  (62, 23, 'https://images.unsplash.com/photo-1622497170185-5d668f816a56?q=80&w=900&fit=crop&auto=format', 1),
  (63, 23, 'https://images.unsplash.com/photo-1608234808654-2a8875faa7fd?q=80&w=900&fit=crop&auto=format', 2),
  (64, 23, 'https://images.unsplash.com/photo-1740710370552-a49b5b01f80a?q=80&w=900&fit=crop&auto=format', 3),
  (65, 24, 'https://images.unsplash.com/photo-1618886614638-80e3c103d31a?q=80&w=900&fit=crop&auto=format', 1),
  (66, 24, 'https://images.unsplash.com/photo-1617137968427-85924c800a22?q=80&w=900&fit=crop&auto=format', 2),
  (67, 24, 'https://images.unsplash.com/photo-1480429370139-e0132c086e2a?q=80&w=900&fit=crop&auto=format', 3),
  (68, 25, 'https://images.unsplash.com/photo-1604695573706-53170668f6a6?q=80&w=900&fit=crop&auto=format', 1),
  (69, 25, 'https://images.unsplash.com/photo-1598032895455-526c9e347a87?q=80&w=900&fit=crop&auto=format', 2),
  (70, 25, 'https://images.unsplash.com/photo-1786729135070-c37def3b3dfb?q=80&w=900&fit=crop&auto=format', 3);

ALTER TABLE product_images ALTER COLUMN id RESTART WITH 71;

MERGE INTO reviews (id, product_id, user_id, rating, comment, created_at) KEY (id) VALUES
  (1, 1, 3, 5, 'Impeccable tailoring, worth every rupee.', CURRENT_TIMESTAMP),
  (2, 5, 3, 4, 'Fits true to size, très chic.', CURRENT_TIMESTAMP);

ALTER TABLE reviews ALTER COLUMN id RESTART WITH 3;
