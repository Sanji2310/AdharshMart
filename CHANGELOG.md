# Changelog

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
