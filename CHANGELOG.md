# Changelog

## Unreleased
- Removed the Three.js wireframe hero/ambient-background scenes sitewide (they also turned out
  to have never actually rendered in this build environment — the CDN they loaded from was
  blocked by the sandbox's network policy). Replaced with a zero-dependency, pure CSS/inline-SVG
  mandala/rosette motif: a large watermark in the homepage hero and matching corner ornaments on
  every other page, so the "elite" cross-page treatment survives regardless of network/WebGL
  availability. `js/three-hero.js`, `js/ambient-bg.js`, and the self-hosted `js/vendor/three.module.min.js`
  are deleted as unused. The homepage hero's black panel still isn't a product photograph — no
  verified, license-clear "people wearing the product" photo was obtainable in this environment
  (Higgsfield generation is blocked on 0 account credits; live stock-photo fetches are blocked by
  network egress policy) — see README for how to swap in a real photo once one is sourced.
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
