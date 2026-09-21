# Changelog

## Unreleased
- Catalog expanded from 8 to 25 products across 9 categories (added Formalwear and Shirts),
  each with a real, individually-searched Unsplash photo — not a repeated or guessed image.
  Verified every product has a non-empty `imageUrl` and no two products share an image.
- Sale pricing: new nullable `compare_at_price` column (`db/migrations/V5`) on `products`,
  enforced by a CHECK constraint (`compare_at_price IS NULL OR compare_at_price > price`).
  Sellers can set it from the "Add listing" form or inline-edit their existing listings; the
  homepage gained a dedicated "On sale" section, and any on-sale product card/detail page shows
  a `−NN%` badge plus a struck-through reference price. Three listings ship on sale by default.
- Homepage category banner: a full-width "The Autumn/Winter edit" banner promoting Outerwear
  with its own editorial photo, between Featured pieces and the new Sale section.
- Wordmark: switched from a mixed-case serif to tracked-out uppercase (0.16em letter-spacing,
  weight 400) — the logotype pattern shared by Dior, Chanel, Celine — for a more overtly
  luxury brand mark, independent of the body/headline type.
- Admin inventory tab and seller dashboard now reflect the full 25-product catalog; verified
  the "Inventory" tab lists all 25 rows and the seller Listings table all 26 after a live
  test publish.
- Real product/hero photography via the Unsplash API (MCP connector — not Higgsfield, and not a
  raw web fetch, both of which are blocked in this build environment): all 8 product `image_url`
  values, the 4 homepage category tiles, and the hero background are now genuine, individually
  searched Unsplash photos (e.g. "gold-framed aviator-style sunglasses" for the aviator sunglasses
  listing) rather than generic picsum.photos placeholders. Photos match each product's *category*
  (stock marketplace photography of the same style of garment), not the literal listed item —
  full attribution (required by the Unsplash API guidelines) is on the new `photo-credits.jsp`
  page, linked from every page's footer.
- Admin inventory: a new "Inventory" tab on `/admin.jsp` (`GET /api/v1/admin/products`) giving
  admins marketplace-wide visibility into every product across every seller — price, stock,
  seller, status — with a low-stock flag and a working "Remove" button wired to the
  moderation endpoint that already existed server-side but had no UI trigger before this.
- Seller restocking: the seller dashboard's Listings table was previously read-only after a
  product was published — no way to update stock/price or reactivate a removed listing without
  calling the API directly. Added inline Edit (price/stock) and Deactivate/Reactivate actions,
  backed by the existing `PUT`/`DELETE /api/v1/products/{id}` endpoints.
- Removed the Three.js wireframe hero/ambient-background scenes sitewide (they also turned out
  to have never actually rendered in this build environment — the CDN they loaded from was
  blocked by the sandbox's network policy). Replaced with a zero-dependency, pure CSS/inline-SVG
  mandala/rosette motif: a large watermark in the homepage hero and matching corner ornaments on
  every other page, so the "elite" cross-page treatment survives regardless of network/WebGL
  availability. `js/three-hero.js`, `js/ambient-bg.js`, and the self-hosted `js/vendor/three.module.min.js`
  are deleted as unused.
- Global image-error handling: a broken/unreachable product `<img>` now degrades to the same
  neutral panel every thumbnail already shows while loading, instead of the browser's broken-image
  glyph and alt text.
- Fixed mojibake in footer.jspf (a JSP static-include encoding gap) — see web.xml jsp-config.
- Typography: swapped the display face from Fraunces to Bodoni Moda (a high-contrast,
  fashion-masthead serif) and tightened letter-spacing/tracking on headlines, the wordmark,
  and nav links for a more overtly editorial feel.
- Swapped hand-picked Unsplash photo IDs (never verified against a live network fetch, so
  their validity was unknown) for deterministic picsum.photos seed URLs, which are guaranteed
  to resolve — in `seed.sql` and `index.jsp`'s category tiles.
- O1 wishlist/save-for-later: `wishlist_items` table (V4 migration), `WishlistDAO`/`WishlistService`/
  `WishlistServlet` (`GET`/`POST`/`DELETE /api/v1/wishlist`), a "Save for later" button on the
  product detail page, and a new `wishlist.jsp` page linked from the header nav.
- Servlet-layer tests (`AuthServletTest`, `CartServletTest`) exercising real Mockito
  `HttpServletRequest`/`HttpServletResponse` against a real embedded datasource.
- `SECURITY-CHECKLIST.md`: each Section 9 security item verified against the codebase.
- `docs/LOAD_TEST.md` + `scripts/load_test.py`: 10-concurrent-user, 60s load test run against a
  locally deployed WAR (0 errors, p99 ~10.5ms).

## v0.1.0 — MVP scaffold
- Project skeleton: Maven/Servlet/JSP layout, HikariCP-backed H2 datasource, schema/seed scripts.
- F1 authentication (register/login/session, bcrypt, session regeneration on login).
- F2/F3 product listings: seller CRUD, buyer browse/search/filter.
- F4 cart, F5 mock-payment checkout, F6 order history (buyer + seller views).
- F7 admin (users/orders/listing moderation), F8 reviews restricted to delivered orders.
- O2 order status workflow, O3 seller sales dashboard.
- Editorial luxury front-end (JSP + JSTL + vanilla JS/fetch), Three.js decorative hero.
- O4 AI chatbot: `ChatProvider` strategy (mock default, Gemini optional), rate limiting, caching.
- JUnit 5 + Mockito test suite (DAO + service layers), GitHub Actions CI.
