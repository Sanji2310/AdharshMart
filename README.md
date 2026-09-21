# AdharshMart

A multi-seller e-commerce marketplace — Java Servlets · JDBC · Apache Tomcat · H2, built to the
Anna University R2025 Semester 3 Java Capstone specification, with an editorial, Nike/Dior/Farfetch-
inspired storefront on top.

**Live deployment:** _not yet deployed — see [Deployment](#deployment) below._
**Design diagrams:** [`docs/diagrams`](docs/diagrams) (ER, use case, sequence — PlantUML source).
**Figma templates:** [AdharshMart — Luxury Storefront Templates](https://www.figma.com/design/82fdKf5uatmH4Sjtwrawam) —
Homepage, Product Listing, and Product Detail, built to the same design tokens as `css/main.css`.

---

## 1. Problem statement

Sellers list products. Buyers browse, search, add to cart, and purchase. An admin manages users,
orders, and listings. Checkout uses a scope-constrained mock payment confirmation (no third-party
gateway), and an AI chatbot answers product/order FAQ questions. Full feature list: see
[Features](#features) below.

## 2. Architecture

```
Browser (JSP + vanilla JS/fetch, CSS/SVG mandala motif, luxury CSS design system)
        | HTTP request
        v
Filter layer      -> EncodingFilter -> RequestIdFilter -> AuthFilter (session check)
        v
Front Controller  -> per-resource Servlets (AuthServlet, ProductServlet, CartServlet,
                      OrderServlet, ReviewServlet, AdminServlet, SellerServlet, WishlistServlet, ChatServlet)
        v
Service layer     -> business logic, validation (no JDBC here)
        v
DAO layer         -> UserDAO, ProductDAO, OrderDAO, CartDAO, ReviewDAO — ALL SQL lives here,
                      PreparedStatement only, try-with-resources everywhere
        v
Connection Pool    -> HikariCP (Singleton, owned by DataSourceListener)
        v
H2 Database (embedded for dev/test, server mode for deployment)
```

The **AI chatbot** sits alongside the REST API:

```
Chat widget (JS, floating button + panel)
        | fetch('/api/v1/chat', POST {message})
        v
ChatServlet   (validates input, session-scoped)
        v
ChatService   -> per-session rate limit (10/min), input cap, answer cache
        v
ChatProvider (Strategy) -> MockChatProvider (default, no API key) | GeminiChatProvider (real LLM)
        v
Returns JSON { reply: "..." } -> widget renders it
```

See [`docs/diagrams/D3-sequence-diagram.puml`](docs/diagrams/D3-sequence-diagram.puml) for the
full place-order sequence, and [`docs/diagrams`](docs/diagrams) for the ER and use-case diagrams.

### Design patterns used

| Pattern | Where |
|---|---|
| DAO | `dao/*DAO.java` interfaces + `*DAOImpl` |
| Front Controller | Per-resource `@WebServlet`s under `controller/`, all behind the `/api/v1` envelope |
| Singleton | `HikariDataSource`, owned by `listener/DataSourceListener` |
| Factory | `dao/DAOFactory`, `service/ServiceFactory`, `service/chat/ChatProviderFactory` |
| Strategy | `service/chat/ChatProvider` (Mock vs. Gemini), `service/PaymentStrategy` (mock payment) |
| Builder | `dto/OrderResponseDTO.Builder` |

## 3. Tech stack

| Component | Choice |
|---|---|
| JDK | 17 |
| Servlet container | Tomcat 9.0.x (`javax.servlet.*`) |
| Build tool | Maven |
| Database | H2 — embedded (`jdbc:h2:mem:...`) for dev/test, server mode for deployment |
| Connection pooling | HikariCP |
| View layer | JSP + JSTL (page shell, session state) + vanilla JS/`fetch()` (API-backed content) |
| Decorative motif | Pure CSS/inline-SVG mandala watermark (hero + sitewide corners) — no JS, no network dependency |
| JSON | Gson, fixed `{success, data, error}` envelope under `/api/v1` |
| Password hashing | jBCrypt |
| AI chatbot | Pluggable `ChatProvider`: `mock` (default) or `gemini` |
| Testing | JUnit 5 + Mockito; DAO tests against `jdbc:h2:mem:test` |
| Logging | SLF4J + Logback, request-id MDC |
| CI | GitHub Actions (`.github/workflows/build.yml`) — `mvn -B clean verify` on every push |

## 4. Features

| ID | Requirement | Status |
|---|---|---|
| F1 | Register/login, BUYER/SELLER roles, seeded ADMIN | Done |
| F2 | Seller product CRUD (create, edit, restock/reprice, deactivate/reactivate, sale pricing) | Done |
| F3 | Buyer browse/search/filter | Done |
| F4 | Cart add/update/remove + running total | Done |
| F5 | Checkout via mock payment | Done |
| F6 | Buyer order history, seller incoming orders | Done |
| F7 | Admin: view users/orders, marketplace-wide inventory, moderate listings | Done |
| F8 | Reviews/ratings on delivered orders | Done |
| O2 | Order status workflow (Pending→Confirmed→Shipped→Delivered) | Done |
| O3 | Seller sales dashboard | Done |
| O4 | AI chatbot | Done |
| O1 | Wishlist / save-for-later | Done |

## 5. Setup instructions

```bash
git clone <repo-url> adharshmart && cd adharshmart
cp src/main/resources/config.properties.example src/main/resources/config.properties  # optional — sane defaults work out of the box
mvn clean package
```

Deploy `target/adharshmart.war` to Tomcat 9's `webapps/`, or run it against an embedded H2
in-memory database with zero setup (the default `db.url` already points at
`jdbc:h2:mem:adharshmart`, and `schema.sql`/`seed.sql` at the repo root run automatically on
first boot).

**Demo accounts** (seeded by `seed.sql`):

| Role | Email | Password |
|---|---|---|
| Admin | `admin@adharshmart.com` | `AdminPass123!` |
| Seller | `seller@adharshmart.com` | `SellerPass123!` |
| Buyer | `buyer@adharshmart.com` | `BuyerPass123!` |

Run tests: `mvn test`. Run the full verify (tests + Checkstyle + SpotBugs): `mvn clean verify`.

### Enabling the real AI chatbot

By default the chatbot runs on `MockChatProvider` (canned FAQ answers, no network call). To use
Gemini instead, set (via `config.properties` or environment variables — never hardcoded, never
shipped to the client):

```
ai.chatbot.provider=gemini
ai.chatbot.apiKey=<your key>
```

## 6. Deployment

Not yet deployed to a public URL. To deploy (see the capstone spec §10 for the full reference
setup): provision a VM, install JDK 17 + Tomcat 9, run H2 in server mode
(`org.h2.tools.Server -tcp -tcpAllowOthers`), execute `schema.sql`/`seed.sql` against it once,
`mvn clean package`, and drop `adharshmart.war` into Tomcat's `webapps/`.

`GET /api/v1/health` returns `{"status":"UP","db":"UP"}` once live — use it for uptime monitoring.

## 7. Security checklist

Full item-by-item verification (with the actual grep commands run) lives in
[`SECURITY-CHECKLIST.md`](SECURITY-CHECKLIST.md). Summary:

- Every query uses `PreparedStatement` — verified via `grep -rn "Statement)" src/`.
- Passwords are bcrypt-hashed (`jBCrypt`), never logged.
- All protected servlets/pages are gated by `AuthFilter` (session check), covered by
  `AuthServletTest`/`CartServletTest`.
- Session id is regenerated on login (`AuthServlet.login`).
- User-supplied content is rendered via `textContent`/DOM APIs client-side and JSTL `<c:out>`
  server-side — never string-concatenated into HTML.
- Error pages (`404.jsp`, `500.jsp`) never expose stack traces.
- `config.properties`/`.env` are gitignored; only `.example` templates are committed.

Load testing (10 concurrent users / 60s, Section 9) has been run — see
[`docs/LOAD_TEST.md`](docs/LOAD_TEST.md) (zero errors, p99 ~10.5ms) and
[`scripts/load_test.py`](scripts/load_test.py) to reproduce.

## 8. Known limitations

- No live deployment yet — see [Deployment](#deployment).
- Product and hero imagery is real photography sourced from the Unsplash API (`image_url` is a
  plain string column, not a binary upload, per the spec's schema) — matched by product
  *category* (e.g. a real photo of aviator sunglasses for the Aviator Sunglasses listing), not the
  literal SKU, since these are stock marketplace sellers rather than a single photographed
  catalog. Full attribution is on `/photo-credits.jsp` (linked from every footer), required by the
  Unsplash API guidelines. Swap `seed.sql`'s `image_url` values for the seller's own product
  photography once real sellers are onboarded.

## 9. Repository layout

```
com.adharsh.adharshmart
 |-- controller   (Servlets — thin, no SQL, no business logic)
 |-- service      (business rules, orchestration; service/chat holds the ChatProvider strategy)
 |-- dao          (interfaces + JDBC implementations)
 |-- model        (POJOs / entities)
 |-- dto          (request/response shapes for JSON endpoints)
 |-- filter       (auth, logging, encoding)
 |-- listener     (DataSource init/teardown)
 |-- util         (PasswordUtil, ValidationUtil, JsonUtil, AppConfig)
 `-- exception    (custom checked exceptions)
```
