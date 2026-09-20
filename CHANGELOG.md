# Changelog

## Unreleased
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
